public class ProjectFile {
	private String filePath;
	private String nameFile;
	private String dataCreazione;
	private String ultimaModifica;
	private double sizeFile;
	
	public ProjectFile(String filePath, String nameFile, String dataCreazione, String ultimaModifica, double sizeFile) {
		this.filePath = filePath;
		this.nameFile = nameFile;
		this.dataCreazione = dataCreazione;
		this.ultimaModifica = ultimaModifica;
		this.sizeFile = sizeFile;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getNameFile() {
		return nameFile;
	}

	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}

	public String getDateCreazione() {
		return dataCreazione;
	}

	public void setDateCreazione(String dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public String getUltimaModifica() {
		return ultimaModifica;
	}

	public void setUltimaModifica(String ultimaModifica) {
		this.ultimaModifica = ultimaModifica;
	}

	public double getSizeFile() {
		return sizeFile;
	}

	public void setSizeFile(double sizeFile) {
		this.sizeFile = sizeFile;
	}
	
	
	
}
