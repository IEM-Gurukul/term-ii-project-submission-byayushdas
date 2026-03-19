package repository;

import exception.PortfolioPersistenceException;
import model.Investment;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based implementation of InvestmentDAO.
 * Serializes portfolio to a .dat file and deserializes on load.
 */
public class FileInvestmentRepository implements InvestmentDAO {

    private final String filePath;

    public FileInvestmentRepository(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void save(List<Investment> investments) throws PortfolioPersistenceException {
        File file = new File(filePath);
        // Create parent directories if they don't exist
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(investments);
        } catch (IOException e) {
            throw new PortfolioPersistenceException("Failed to save portfolio: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Investment> load() throws PortfolioPersistenceException {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>(); // No saved data yet — return empty list
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Investment>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new PortfolioPersistenceException("Failed to load portfolio: " + e.getMessage(), e);
        }
    }
}
