package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ReservaResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class TicketUtil {

    public static void generarArchivosReserva(ReservaResponse res) {
        String carpetaRuta = "C:/elsultan_tickets/";
        File carpeta = new File(carpetaRuta);
        if (!carpeta.exists()) carpeta.mkdirs();

        // 1. Configuración de GSON para Java 17+ (LocalDate y LocalTime)
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(java.time.LocalDate.class, (com.google.gson.JsonSerializer<java.time.LocalDate>) 
                    (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.toString()))
                .registerTypeAdapter(java.time.LocalTime.class, (com.google.gson.JsonSerializer<java.time.LocalTime>) 
                    (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.toString()))
                .create();

        String jsonStr = gson.toJson(res);
        
        // 2. Generar el archivo JSON (PrintWriter es excelente para texto/json)
        File fileJson = new File(carpetaRuta + "reserva_" + res.getId() + ".json");
        try (PrintWriter pw = new PrintWriter(fileJson, StandardCharsets.UTF_8)) {
            pw.write(jsonStr);
            System.out.println("JSON generado: " + fileJson.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 3. Generar Ticket de Texto LEGIBLE (Usando BufferedWriter para mejor compatibilidad)
        File fileTxt = new File(carpetaRuta + "ticket_" + res.getId() + ".txt");
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileTxt), StandardCharsets.UTF_8))) {
            
            bw.write("=== COMPROBANTE DE RESERVA EL SULTAN ===");
            bw.newLine();
            bw.write("ID: " + res.getId());
            bw.newLine();
            bw.write("Cliente: " + res.getNombreCliente() + " " + res.getApellidoCliente());
            bw.newLine();
            bw.write("Mesa Nro: " + res.getNumeroMesa());
            bw.newLine();
            bw.write("Fecha y Hora: " + res.getFecha() + " a las " + res.getHora());
            bw.newLine();
            bw.write("Estado: CONFIRMADO");
            bw.newLine();
            bw.write("========================================");
            
            System.out.println("TXT generado: " + fileTxt.getAbsolutePath());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}