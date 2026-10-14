package co.istad.business.document;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessContactDocument {
    private String email;
    private String phone;
    private String address;
}
