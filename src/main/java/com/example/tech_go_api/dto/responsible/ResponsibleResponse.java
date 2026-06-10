package com.example.tech_go_api.dto.responsible;

import java.util.List;

public class ResponsibleResponse {

    private String id;
    private String name;
    private String phone;
    private String email;
    private String document;
    private String address;
    private String addressNumber;
    private String addressNeighborhood;
    private String addressComplement;
    private String postcode;
    private List<ResponsibleStudentSummary> students;

    public ResponsibleResponse(String id, String name, String phone, String email, String document,
                               String address, String addressNumber, String addressNeighborhood,
                               String addressComplement, String postcode,
                               List<ResponsibleStudentSummary> students) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.document = document;
        this.address = address;
        this.addressNumber = addressNumber;
        this.addressNeighborhood = addressNeighborhood;
        this.addressComplement = addressComplement;
        this.postcode = postcode;
        this.students = students;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getDocument() { return document; }
    public String getAddress() { return address; }
    public String getAddressNumber() { return addressNumber; }
    public String getAddressNeighborhood() { return addressNeighborhood; }
    public String getAddressComplement() { return addressComplement; }
    public String getPostcode() { return postcode; }
    public List<ResponsibleStudentSummary> getStudents() { return students; }
}
