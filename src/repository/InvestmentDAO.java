package repository;

import exception.PortfolioPersistenceException;
import model.Investment;
import java.util.List;

/**
 * DAO interface for Investment persistence.
 * Decouples service layer from storage implementation.
 */
public interface InvestmentDAO {
    void save(List<Investment> investments) throws PortfolioPersistenceException;
    List<Investment> load() throws PortfolioPersistenceException;
}
