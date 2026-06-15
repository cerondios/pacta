package com.pacta.pacta_app.deal.application;

import com.pacta.pacta_app.property.domain.Property;
import com.pacta.pacta_app.shared.domain.DateUtil;
import com.pacta.pacta_app.user.domain.User;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/** Renders the plain-text rental contract that both parties sign inside "Mis negocios". */
@Component
public class ContractPdfGenerator {

    private static final PDType1Font BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final PDType1Font BODY = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final float MARGIN = 56f;
    private static final float LEADING = 16f;

    public byte[] generate(Property property, User landlord, User tenant) {
        List<String> lines = buildLines(property, landlord, tenant);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            float y = page.getMediaBox().getHeight() - MARGIN;

            PDPageContentStream content = new PDPageContentStream(document, page);
            for (String line : lines) {
                if (y < MARGIN) {
                    content.close();
                    page = new PDPage(PDRectangle.LETTER);
                    document.addPage(page);
                    y = page.getMediaBox().getHeight() - MARGIN;
                    content = new PDPageContentStream(document, page);
                }
                boolean heading = line.startsWith("# ");
                String text = heading ? line.substring(2) : line;

                content.beginText();
                content.setFont(heading ? BOLD : BODY, heading ? 13 : 11);
                content.newLineAtOffset(MARGIN, y);
                content.showText(text);
                content.endText();
                y -= heading ? LEADING + 6 : LEADING;
            }
            content.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to generate contract PDF", e);
        }
    }

    private List<String> buildLines(Property property, User landlord, User tenant) {
        List<String> lines = new ArrayList<>();
        lines.add("# CONTRATO DE ARRENDAMIENTO");
        lines.add("Generado por Pacta el " + DateUtil.now());
        lines.add("");
        lines.add("# 1. Partes");
        lines.add("Arrendador: " + landlord.getFullName() + " (" + landlord.getEmail() + ")");
        lines.add("Arrendatario: " + tenant.getFullName() + " (" + tenant.getEmail() + ")");
        lines.add("");
        lines.add("# 2. Inmueble");
        lines.add("Direccion: " + safe(property.getAddress()));
        lines.add("Barrio/Ciudad: " + safe(property.getNeighborhood()) + ", " + safe(property.getCity()) + ", " + safe(property.getCountry()));
        lines.add("Tipo: " + safe(property.getType() != null ? property.getType().name() : null));
        lines.add("");
        lines.add("# 3. Condiciones economicas");
        lines.add("Canon mensual: " + formatMoney(property.getMonthlyRent(), property.getCurrency()));
        if (property.getAdminFee() != null) {
            lines.add("Administracion: " + formatMoney(property.getAdminFee(), property.getCurrency()) + " (no incluida en el canon)");
        } else {
            lines.add("Administracion: incluida en el canon");
        }
        if (property.getMinContractMonths() != null) {
            lines.add("Plazo minimo: " + property.getMinContractMonths() + " meses");
        }
        lines.add("");
        lines.add("# 4. Aceptacion");
        lines.add("Ambas partes declaran haber leido y aceptado las condiciones de este contrato,");
        lines.add("y lo formalizan mediante firma digital dentro de la plataforma Pacta.");
        return lines;
    }

    private static String safe(String value) {
        return value != null ? value : "—";
    }

    private static String formatMoney(Long amount, String currency) {
        if (amount == null) return "—";
        return String.format("%,d %s", amount, currency != null ? currency : "");
    }
}
