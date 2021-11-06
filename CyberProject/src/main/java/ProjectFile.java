public class ProjectFile {
	private String filePath;
	private String nameFile;
	private String dataCreazione;
	private String ultimaModifica;
	
	public ProjectFile(String filePath, String nameFile, String dataCreazione, String ultimaModifica) {
		this.filePath = filePath;
		this.nameFile = nameFile;
		this.dataCreazione = dataCreazione;
		this.ultimaModifica = ultimaModifica;
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
	
	
	
}
