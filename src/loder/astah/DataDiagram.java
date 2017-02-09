package loder.astah;

import java.util.List;

public interface DataDiagram {
	
	abstract List<Table> getTables();
	abstract String getErrorMessage();
}
