package com.example.tech_go_api.services.product;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.tech_go_api.domain.product.Product;
import com.example.tech_go_api.domain.product.Sale;
import com.example.tech_go_api.domain.profileplayer.ProfilePlayer;
import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.staff.Permission;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.product.SaleCreateRequestDTO;
import com.example.tech_go_api.dto.product.SaleResponseDTO;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.product.SaleRepository;
import com.example.tech_go_api.repositories.profileplayer.ProfilePlayerRepository;
import com.example.tech_go_api.services.school.SchoolResolverService;
import com.example.tech_go_api.services.staff.PermissionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProfilePlayerRepository profilePlayerRepository;
    private final ProductService productService;
    private final SchoolResolverService schoolResolverService;
    private final PermissionService permissionService;

    public SaleResponseDTO create(SaleCreateRequestDTO dto, User user) {
        permissionService.requirePermission(user, Permission.PRODUTOS_VENDER);
        School school = schoolResolverService.schoolOf(user);

        boolean hasBuyerPlayer = dto.buyerPlayerId() != null && !dto.buyerPlayerId().isBlank();
        boolean hasBuyerName = dto.buyerName() != null && !dto.buyerName().isBlank();
        if (hasBuyerPlayer == hasBuyerName) {
            throw new IllegalArgumentException("Informe um aluno OU o nome do comprador, não os dois nem nenhum.");
        }

        Product product = productService.findOwnedProduct(dto.productId(), user);

        Sale sale = new Sale();
        sale.setSchool(school);
        sale.setProduct(product);
        sale.setQuantity(dto.quantity());
        sale.setUnitPrice(product.getPrice());
        sale.setTotalAmount(product.getPrice().multiply(java.math.BigDecimal.valueOf(dto.quantity())));
        sale.setPaymentMethod(dto.paymentMethod());
        sale.setSoldAt(dto.soldAt() != null ? dto.soldAt() : LocalDate.now());

        if (hasBuyerPlayer) {
            ProfilePlayer player = profilePlayerRepository.findById(dto.buyerPlayerId())
                    .orElseThrow(() -> new NotFoundException("Aluno não encontrado"));
            if (!player.getSchool().getId().equals(school.getId())) {
                throw new IllegalArgumentException("Este aluno não pertence à sua escola.");
            }
            sale.setBuyerPlayer(player);
        } else {
            sale.setBuyerName(dto.buyerName());
        }

        return toResponse(saleRepository.save(sale));
    }

    public List<SaleResponseDTO> findAll(User user, String month) {
        permissionService.requirePermission(user, Permission.PRODUTOS_VER);
        School school = schoolResolverService.schoolOf(user);
        YearMonth yearMonth = parseMonth(month);

        return saleRepository
                .findBySchoolAndSoldAtBetweenOrderBySoldAtDesc(school, yearMonth.atDay(1), yearMonth.atEndOfMonth())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void delete(String id, User user) {
        permissionService.requirePermission(user, Permission.PRODUTOS_EXCLUIR_VENDA);
        School school = schoolResolverService.schoolOf(user);

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venda não encontrada"));

        if (!sale.getSchool().getId().equals(school.getId())) {
            throw new IllegalArgumentException("Esta venda não pertence à sua escola.");
        }

        saleRepository.delete(sale);
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

    private SaleResponseDTO toResponse(Sale sale) {
        String buyerDisplayName = sale.getBuyerPlayer() != null
                ? (sale.getBuyerPlayer().getFirstname() + " " + sale.getBuyerPlayer().getLastname()).trim()
                : sale.getBuyerName();

        return new SaleResponseDTO(
                sale.getId(),
                sale.getProduct().getId(),
                sale.getProduct().getName(),
                sale.getQuantity(),
                sale.getUnitPrice(),
                sale.getTotalAmount(),
                sale.getBuyerPlayer() != null ? sale.getBuyerPlayer().getId() : null,
                buyerDisplayName,
                sale.getPaymentMethod(),
                sale.getSoldAt()
        );
    }
}
