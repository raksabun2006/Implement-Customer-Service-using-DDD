package co.istad.business.mapper;

import co.istad.Money;
import co.istad.business.document.BusinessContactDocument;
import co.istad.business.document.BusinessDocument;
import co.istad.business.document.ProductDocument;
import co.istad.business.domain.entity.Business;
import co.istad.business.domain.entity.Product;
import co.istad.business.domain.valueobject.BusinessContact;
import co.istad.business.domain.valueobject.BusinessId;
import co.istad.business.domain.valueobject.ProductId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BusinessPersistenceMapper {

    public BusinessDocument toDocument(Business business) {
        if (business == null) {
            return null;
        }

        BusinessContactDocument contactDoc = null;
        if (business.getContact() != null) {
            contactDoc = BusinessContactDocument.builder()
                    .email(business.getContact().email())
                    .phone(business.getContact().phone())
                    .address(business.getContact().address())
                    .build();
        }

        List<ProductDocument> productDocs = new ArrayList<>();
        if (business.getProducts() != null) {
            for (Product p : business.getProducts()) {
                ProductDocument pDoc = ProductDocument.builder()
                        .id(p.getId() != null ? p.getId().value() : null)
                        .name(p.getName())
                        .price(p.getPrice() != null ? p.getPrice().amount() : null)
                        .quantity(p.getQuantity())
                        .available(p.isAvailable())
                        .build();
                productDocs.add(pDoc);
            }
        }

        return BusinessDocument.builder()
                .id(business.getId() != null ? business.getId().value() : null)
                .name(business.getName())
                .contact(contactDoc)
                .active(business.isActive())
                .products(productDocs)
                .build();
    }

    public Business toDomain(BusinessDocument document) {
        if (document == null) {
            return null;
        }

        BusinessContact contact = null;
        if (document.getContact() != null) {
            contact = new BusinessContact(
                    document.getContact().getEmail(),
                    document.getContact().getPhone(),
                    document.getContact().getAddress()
            );
        }

        List<Product> products = new ArrayList<>();
        if (document.getProducts() != null) {
            for (ProductDocument pDoc : document.getProducts()) {
                Product product = new Product(
                        pDoc.getId() != null ? new ProductId(pDoc.getId()) : null,
                        pDoc.getName(),
                        pDoc.getPrice() != null ? new Money(pDoc.getPrice()) : null,
                        pDoc.getQuantity(),
                        pDoc.isAvailable()
                );
                products.add(product);
            }
        }

        return Business.builder()
                .id(document.getId() != null ? new BusinessId(document.getId()) : null)
                .name(document.getName())
                .contact(contact)
                .active(document.isActive())
                .products(products)
                .build();
    }
}
