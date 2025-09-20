package de.kaleidox.workbench.model.jpa.representant;

import de.kaleidox.workbench.repo.CustomerRepository;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;

import static de.kaleidox.workbench.util.ApplicationContextProvider.*;

@Data
@Entity
@EqualsAndHashCode(of = "name")
//@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "department" }) })
public class Customer {
    @Id String name;
    //@Nullable String department;

    @Override
    public String toString() {
        return name;
    }

    @Value
    public static class Converter implements AttributeConverter<Customer, String> {
        @Override
        public String convertToDatabaseColumn(Customer customer) {
            return customer.name;
        }

        @Override
        public Customer convertToEntityAttribute(String customer) {
            return bean(CustomerRepository.class).findById(customer).orElseThrow();
        }
    }
}
