package com.example.tech_go_api.services.ledger;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.tech_go_api.domain.ledger.LedgerCategory;
import com.example.tech_go_api.domain.ledger.LedgerEntry;
import com.example.tech_go_api.domain.ledger.LedgerType;
import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.ledger.LedgerCategoryCreateRequestDTO;
import com.example.tech_go_api.dto.ledger.LedgerCategoryResponseDTO;
import com.example.tech_go_api.dto.ledger.LedgerCategoryTotalDTO;
import com.example.tech_go_api.dto.ledger.LedgerCategoryUpdateRequestDTO;
import com.example.tech_go_api.dto.ledger.LedgerEntryRequestDTO;
import com.example.tech_go_api.dto.ledger.LedgerEntryResponseDTO;
import com.example.tech_go_api.exceptions.ConflictException;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.ledger.LedgerCategoryRepository;
import com.example.tech_go_api.repositories.ledger.LedgerEntryRepository;
import com.example.tech_go_api.services.school.SchoolResolverService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LedgerService {

    public record MonthTotals(
            List<LedgerCategoryTotalDTO> entradas,
            List<LedgerCategoryTotalDTO> saidas,
            BigDecimal totalEntradas,
            BigDecimal totalSaidas
    ) {}

    private final LedgerCategoryRepository categoryRepository;
    private final LedgerEntryRepository entryRepository;
    private final SchoolResolverService schoolResolverService;

    public LedgerCategoryResponseDTO createCategory(LedgerCategoryCreateRequestDTO dto, User user) {
        School school = schoolResolverService.schoolOf(user);
        String name = dto.name().trim();

        var existing = categoryRepository.findBySchoolAndTypeAndNameIgnoreCase(school, dto.type(), name);
        if (existing.isPresent()) {
            LedgerCategory category = existing.get();
            if (category.isActive()) {
                throw new ConflictException("Já existe uma categoria com esse nome");
            }
            category.setActive(true);
            return toResponse(categoryRepository.save(category));
        }

        LedgerCategory category = new LedgerCategory();
        category.setSchool(school);
        category.setName(name);
        category.setType(dto.type());
        category.setActive(true);
        return toResponse(categoryRepository.save(category));
    }

    public LedgerCategoryResponseDTO updateCategory(String id, LedgerCategoryUpdateRequestDTO dto, User user) {
        LedgerCategory category = findOwnedCategory(id, user);
        String name = dto.name().trim();

        categoryRepository.findBySchoolAndTypeAndNameIgnoreCase(category.getSchool(), category.getType(), name)
                .filter(other -> !other.getId().equals(category.getId()))
                .ifPresent(other -> {
                    throw new ConflictException("Já existe uma categoria com esse nome");
                });

        category.setName(name);
        return toResponse(categoryRepository.save(category));
    }

    public List<LedgerCategoryResponseDTO> findCategories(LedgerType type, User user) {
        School school = schoolResolverService.schoolOf(user);
        return categoryRepository.findBySchoolAndTypeAndActiveOrderByNameAsc(school, type, true).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void deactivateCategory(String id, User user) {
        LedgerCategory category = findOwnedCategory(id, user);
        category.setActive(false);
        categoryRepository.save(category);
    }

    public LedgerEntryResponseDTO createEntry(LedgerEntryRequestDTO dto, User user) {
        School school = schoolResolverService.schoolOf(user);
        LedgerCategory category = findOwnedCategory(dto.categoryId(), user);
        if (!category.isActive()) {
            throw new IllegalArgumentException("Esta categoria está inativa.");
        }

        LedgerEntry entry = new LedgerEntry();
        entry.setSchool(school);
        apply(entry, dto, category);
        return toResponse(entryRepository.save(entry));
    }

    public LedgerEntryResponseDTO updateEntry(String id, LedgerEntryRequestDTO dto, User user) {
        LedgerEntry entry = findOwnedEntry(id, user);
        LedgerCategory category = findOwnedCategory(dto.categoryId(), user);
        if (category.getType() != entry.getCategory().getType()) {
            throw new IllegalArgumentException("A categoria precisa ser do mesmo tipo do lançamento.");
        }
        apply(entry, dto, category);
        return toResponse(entryRepository.save(entry));
    }

    public void deleteEntry(String id, User user) {
        entryRepository.delete(findOwnedEntry(id, user));
    }

    public List<LedgerEntryResponseDTO> findEntries(String month, LedgerType type, String categoryId, User user) {
        School school = schoolResolverService.schoolOf(user);
        YearMonth yearMonth = parseMonth(month);

        return entryRepository.findBySchoolAndDateBetweenOrderByDateDescIdAsc(
                        school, yearMonth.atDay(1), yearMonth.atEndOfMonth()).stream()
                .filter(e -> type == null || e.getCategory().getType() == type)
                .filter(e -> categoryId == null || categoryId.isBlank() || e.getCategory().getId().equals(categoryId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public MonthTotals monthTotals(School school, YearMonth yearMonth) {
        List<LedgerEntry> entries = entryRepository.findBySchoolAndDateBetweenOrderByDateDescIdAsc(
                school, yearMonth.atDay(1), yearMonth.atEndOfMonth());

        List<LedgerCategoryTotalDTO> entradas = totalsByCategory(entries, LedgerType.ENTRADA);
        List<LedgerCategoryTotalDTO> saidas = totalsByCategory(entries, LedgerType.SAIDA);

        return new MonthTotals(entradas, saidas, sum(entradas), sum(saidas));
    }

    private List<LedgerCategoryTotalDTO> totalsByCategory(List<LedgerEntry> entries, LedgerType type) {
        Map<String, List<LedgerEntry>> byCategory = entries.stream()
                .filter(e -> e.getCategory().getType() == type)
                .collect(Collectors.groupingBy(e -> e.getCategory().getId(), LinkedHashMap::new, Collectors.toList()));

        return byCategory.values().stream()
                .map(list -> new LedgerCategoryTotalDTO(
                        list.get(0).getCategory().getId(),
                        list.get(0).getCategory().getName(),
                        list.stream().map(LedgerEntry::getAmount).filter(Objects::nonNull)
                                .reduce(BigDecimal.ZERO, BigDecimal::add),
                        list.size()))
                .sorted(Comparator.comparing(LedgerCategoryTotalDTO::total).reversed())
                .collect(Collectors.toList());
    }

    private BigDecimal sum(List<LedgerCategoryTotalDTO> totals) {
        return totals.stream().map(LedgerCategoryTotalDTO::total).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void apply(LedgerEntry entry, LedgerEntryRequestDTO dto, LedgerCategory category) {
        entry.setCategory(category);
        entry.setAmount(dto.amount());
        entry.setDate(dto.date());
        entry.setDescription(dto.description() == null || dto.description().isBlank() ? null : dto.description().trim());
        entry.setPaymentMethod(dto.paymentMethod());
    }

    private LedgerCategory findOwnedCategory(String id, User user) {
        School school = schoolResolverService.schoolOf(user);
        LedgerCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));

        if (!category.getSchool().getId().equals(school.getId())) {
            throw new IllegalArgumentException("Esta categoria não pertence à sua escola.");
        }
        return category;
    }

    private LedgerEntry findOwnedEntry(String id, User user) {
        School school = schoolResolverService.schoolOf(user);
        LedgerEntry entry = entryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lançamento não encontrado"));

        if (!entry.getSchool().getId().equals(school.getId())) {
            throw new IllegalArgumentException("Este lançamento não pertence à sua escola.");
        }
        return entry;
    }

    private YearMonth parseMonth(String month) {
        if (month == null || month.isBlank()) {
            return YearMonth.now();
        }
        try {
            return YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (Exception e) {
            return YearMonth.now();
        }
    }

    private LedgerCategoryResponseDTO toResponse(LedgerCategory category) {
        return new LedgerCategoryResponseDTO(category.getId(), category.getName(), category.getType(), category.isActive());
    }

    private LedgerEntryResponseDTO toResponse(LedgerEntry entry) {
        return new LedgerEntryResponseDTO(
                entry.getId(),
                entry.getCategory().getType(),
                entry.getCategory().getId(),
                entry.getCategory().getName(),
                entry.getAmount(),
                entry.getDate(),
                entry.getDescription(),
                entry.getPaymentMethod()
        );
    }
}
