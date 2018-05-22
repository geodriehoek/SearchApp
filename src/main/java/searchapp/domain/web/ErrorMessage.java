package searchapp.domain.web;

public class ErrorMessage {
    private String status;
    private String description;

    public ErrorMessage(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public ErrorMessage(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
