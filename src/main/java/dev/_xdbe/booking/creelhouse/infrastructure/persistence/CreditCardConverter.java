
package dev._xdbe.booking.creelhouse.infrastructure.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;

@Converter
public class CreditCardConverter implements AttributeConverter<String, String> {

    @Autowired
    private CryptographyHelper cryptographyHelper;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isBlank()) {
            return attribute;
        }

        try {
            return CryptographyHelper.encryptData(attribute);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return dbData;
        }

        String pan;
        try {
            pan = cryptographyHelper.decryptData(dbData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return panMasking(pan);
    }

    private String panMasking(String pan) {
        if (pan == null || pan.isBlank()) {
            return pan;
        }

        if (pan.length() <= 8) {
            return pan;
        }

        String first4 = pan.substring(0, 4);
        String last4 = pan.substring(pan.length() - 4);

        StringBuilder masked = new StringBuilder();
        masked.append(first4);

        for (int i = 4; i < pan.length() - 4; i++) {
            masked.append('*');
        }

        masked.append(last4);

        return masked.toString();
    }
}