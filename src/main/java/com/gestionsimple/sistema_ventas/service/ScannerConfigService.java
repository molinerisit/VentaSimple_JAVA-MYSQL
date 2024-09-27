package com.gestionsimple.sistema_ventas.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionsimple.sistema_ventas.model.ScannerConfig;
import com.gestionsimple.sistema_ventas.repository.ScannerConfigRepository;
import com.fazecast.jSerialComm.SerialPort;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScannerConfigService {

    private final ScannerConfigRepository configRepository;

    public ScannerConfigService(ScannerConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    // Método para obtener la configuración actual del escáner
    public ScannerConfig getConfig() {
        return configRepository.findAll().stream().findFirst().orElse(null);
    }

    // Método para guardar la configuración del puerto del escáner
    @Transactional
    public void saveConfig(String portName) {
        ScannerConfig config = getConfig();
        if (config == null) {
            config = new ScannerConfig();
        }
        config.setPortName(portName);
        configRepository.save(config);
    }

    // Método para obtener los puertos seriales disponibles
    public List<String> getAvailablePorts() {
        List<String> availablePorts = new ArrayList<>();
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            availablePorts.add(port.getSystemPortName());
        }
        return availablePorts;
    }

    // Método para leer el código de barras desde el puerto configurado
    public String readBarcode() {
        ScannerConfig config = getConfig();
        if (config != null && config.getPortName() != null) {
            SerialPort port = SerialPort.getCommPort(config.getPortName());
            port.openPort();
            byte[] readBuffer = new byte[1024];
            int numRead = port.readBytes(readBuffer, readBuffer.length);
            port.closePort();
            return new String(readBuffer, 0, numRead).trim(); // Eliminamos espacios en blanco
        }
        return null; // O manejar error si no hay puerto configurado
    }
}
