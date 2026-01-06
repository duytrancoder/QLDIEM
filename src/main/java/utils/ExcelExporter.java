package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for exporting data to Excel files
 */
public class ExcelExporter {

    /**
     * Export JTable data to Excel file with file chooser dialog
     * 
     * @param parent          Parent component for dialog
     * @param tableModel      Table model containing data to export
     * @param sheetName       Name of the Excel sheet
     * @param defaultFileName Default filename for save dialog (without extension)
     * @return true if export successful, false otherwise
     */
    public static boolean exportToExcel(JFrame parent, DefaultTableModel tableModel, String sheetName,
            String defaultFileName) {
        // Create file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");

        // Add timestamp to filename
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String suggestedFileName = defaultFileName + "_" + timestamp + ".xlsx";
        fileChooser.setSelectedFile(new java.io.File(suggestedFileName));

        // Show save dialog
        int userSelection = fileChooser.showSaveDialog(parent);

        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return false; // User cancelled
        }

        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        if (!filePath.toLowerCase().endsWith(".xlsx")) {
            filePath += ".xlsx";
        }

        try {
            // Create workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(sheetName);

            // Create header row with bold font
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Write header
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(tableModel.getColumnName(col));
                cell.setCellStyle(headerStyle);
            }

            // Create cell style for data
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Write data rows
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                Row dataRow = sheet.createRow(row + 1);
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    Cell cell = dataRow.createCell(col);
                    Object value = tableModel.getValueAt(row, col);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                    cell.setCellStyle(dataStyle);
                }
            }

            // Auto-size columns
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                sheet.autoSizeColumn(col);
                // Add a bit of extra width
                sheet.setColumnWidth(col, sheet.getColumnWidth(col) + 1000);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            workbook.close();

            // Show success message
            JOptionPane.showMessageDialog(parent,
                    "Xuất file Excel thành công!\nĐã lưu tại: " + filePath,
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            return true;

        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent,
                    "Lỗi khi xuất file Excel:\n" + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Export with default sheet name "Data"
     */
    public static boolean exportToExcel(JFrame parent, DefaultTableModel tableModel, String defaultFileName) {
        return exportToExcel(parent, tableModel, "Data", defaultFileName);
    }
}
