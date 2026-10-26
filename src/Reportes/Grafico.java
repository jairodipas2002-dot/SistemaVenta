
package Reportes;

import Modelo.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class Grafico {

    public static void graficar(Date fechaSeleccionada) {

        if (fechaSeleccionada == null) {
            JOptionPane.showMessageDialog(
                null, "Selecciona una fecha en el calendario."
            );
            return;
        }

        java.util.Calendar calendario = java.util.Calendar.getInstance();
        calendario.setTime(fechaSeleccionada);
        int anio = calendario.get(java.util.Calendar.YEAR);

        String[] meses = {
            "Enero", "Febrero", "Marzo", "Abril",
            "Mayo", "Junio", "Julio", "Agosto",
            "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Inicializar los 12 meses, incluso si no hubo ventas
        for (String mes : meses) {
            dataset.addValue(0.0, "Total vendido (S/.)", mes);
        }
//Esta consulta convierte la fecha almacenada en texto a una fecha que MySQL pueda interpretar
        String sql = "SELECT MONTH(fecha_convertida) AS mes, "
                   + "SUM(total) AS total_mes "
                   + "FROM (SELECT STR_TO_DATE(fecha, '%d/%m/%Y') "
                   + "AS fecha_convertida, total FROM ventas) AS datos "
                   + "WHERE fecha_convertida IS NOT NULL "
                   + "AND YEAR(fecha_convertida) = ? "
                   + "GROUP BY MONTH(fecha_convertida) "
                   + "ORDER BY mes";

        Conexion cn = Conexion.getInstancia();

        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, anio);

            try (ResultSet rs = ps.executeQuery()) {
                boolean hayVentas = false;

                while (rs.next()) {
                    int numeroMes = rs.getInt("mes");
                    double totalMes = rs.getDouble("total_mes");

                    dataset.setValue(
                        totalMes,
                        "Total vendido (S/.)",
                        meses[numeroMes - 1]
                    );

                    hayVentas = true;
                }

                if (!hayVentas) {
                    JOptionPane.showMessageDialog(
                        null,
                        "No hay ventas registradas para el año " + anio + "."
                    );
                    return;
                }
            }

            JFreeChart grafico = ChartFactory.createBarChart(
                "Ventas mensuales del año " + anio,
                "Mes",
                "Total vendido (S/.)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
            );



            ChartFrame ventana = new ChartFrame(
                "Reporte de ventas mensuales",
                grafico
            );

            ventana.setSize(1000, 550);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                null,
                "Error al generar el reporte: " + e.getMessage()
            );
        }
    }
}