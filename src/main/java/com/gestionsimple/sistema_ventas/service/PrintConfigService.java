package com.gestionsimple.sistema_ventas.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fazecast.jSerialComm.SerialPort;
import com.gestionsimple.sistema_ventas.model.PrintConfig;
import com.gestionsimple.sistema_ventas.repository.PrintConfigRepository;

@Service
public class PrintConfigService {

    private final PrintConfigRepository printConfigRepository;

    public PrintConfigService(PrintConfigRepository printConfigRepository) {
        this.printConfigRepository = printConfigRepository;
    }

    // Método para obtener la configuración actual de la impresora
    public PrintConfig getConfig() {
        return printConfigRepository.findAll().stream().findFirst().orElse(null);
    }

    // Método para guardar la configuración del puerto de la impresora
    @Transactional
    public void saveConfig(String portName) {
        PrintConfig config = getConfig();
        if (config == null) {
            config = new PrintConfig();
        }
        config.setPortName(portName);
        printConfigRepository.save(config);
    }

    // Método para obtener los puertos seriales y las impresoras USB disponibles
    public List<String> getAvailablePorts() {
        List<String> availablePorts = new ArrayList<>();

        // Listar puertos COM
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            availablePorts.add(port.getSystemPortName());
        }

        // Listar impresoras USB disponibles
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            availablePorts.add(printService.getName());  // Agrega el nombre de la impresora
        }

        return availablePorts;
    }

    // Método para imprimir el recibo
    public void printReceipt(String receiptData) {
        PrintConfig config = getConfig();
        if (config != null && config.getPortName() != null) {
            if (config.getPortName().startsWith("COM")) {
                // Si es un puerto COM, imprimir vía puerto serial
                SerialPort port = SerialPort.getCommPort(config.getPortName());
                if (port.openPort()) {
                    byte[] data = receiptData.getBytes();
                    port.writeBytes(data, data.length);
                    port.closePort();
                } else {
                    throw new RuntimeException("No se pudo abrir el puerto: " + config.getPortName());
                }
            } else {
                // Si es una impresora USB, imprimir usando el nombre de la impresora
                printToUSB(config.getPortName(), receiptData);
            }
        } else {
            throw new RuntimeException("No hay un puerto configurado para la impresión");
        }
    }

    // Método para imprimir en una impresora USB
    private void printToUSB(String printerName, String receiptData) {
        try {
            // Crear un archivo temporal con el contenido del ticket
            File tempFile = File.createTempFile("ticket", ".txt");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(receiptData);
            }

            // Log para verificar el contenido del archivo
            System.out.println("Contenido del archivo temporal: " + receiptData);

            // Buscar el servicio de impresión correspondiente al nombre de la impresora
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
            PrintService selectedService = null;

            for (PrintService service : printServices) {
                // Comparar con el nombre de la impresora
                if (service.getName().equalsIgnoreCase(printerName)) {
                    selectedService = service;
                    break;
                }
            }

            if (selectedService != null) {
                // Crear un documento para imprimir
                DocPrintJob printJob = selectedService.createPrintJob();
                DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;

                // Abrir el archivo y enviarlo a la impresora
                try (InputStream inputStream = new FileInputStream(tempFile)) {
                    Doc doc = new SimpleDoc(inputStream, flavor, null);
                    printJob.print(doc, null);
                    System.out.println("Impresión enviada a la impresora: " + printerName);
                } catch (Exception e) {
                    throw new RuntimeException("Error al enviar el documento a la impresora: " + e.getMessage());
                }
            } else {
                throw new RuntimeException("No se encontró la impresora: " + printerName);
            }

            // Eliminar el archivo temporal después de la impresión
            tempFile.delete();
        } catch (Exception e) {
            throw new RuntimeException("Error al imprimir en la impresora USB: " + e.getMessage());
        }
    }

}
