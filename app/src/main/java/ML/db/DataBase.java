package ML.db;
import java.util.List;
/**
 * @author Ganesh Tiwari
 */
public interface DataBase {

	void setType(String type);

	List<String> readRegistered();

	Model readModel(String name) throws Exception;

	void saveModel(Model m, String name) throws Exception;
}