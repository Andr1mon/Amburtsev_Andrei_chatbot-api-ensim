package fr.ensim.interop.introrest.model.openWeather;

public class Example {
	private String pathVar;
	private String queryVar;

	private Example(String pathVar, String queryVar) {
		super();
		this.pathVar = pathVar;
		this.queryVar = queryVar;
	}

	public String getPathVar() {
		return pathVar;
	}

	public void setPathVar(String pathVar) {
		this.pathVar = pathVar;
	}

	public String getQueryVar() {
		return queryVar;
	}

	public void setQueryVar(String queryVar) {
		this.queryVar = queryVar;
	}

	public static Example of(String pathVar, String queryVar) {
		return new Example(pathVar, queryVar);
	}

	@Override
	public String toString() {
		return "Example [pathVar=" + pathVar + ", queryVar=" + queryVar + "]";
	}

}
