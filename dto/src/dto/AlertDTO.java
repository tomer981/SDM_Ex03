package dto;

public class AlertDTO {
    private StoreDTO storeDTO = null;
    private FeedbackDTO feedbackDTO = null;
    private SubOrderDTO subOrderDTO = null;

    public AlertDTO(FeedbackDTO feedbackDTO) {
        this.feedbackDTO = feedbackDTO;
    }
    public AlertDTO(StoreDTO storeDTO) {
        this.storeDTO = storeDTO;
    }
    public AlertDTO(SubOrderDTO subOrderDTO) {
        this.subOrderDTO = subOrderDTO;
    }


    public StoreDTO getStoreDTO() {
        return storeDTO;
    }
    public FeedbackDTO getFeedbackDTO() {
        return feedbackDTO;
    }
    public SubOrderDTO getSubOrderDTO() {
        return subOrderDTO;
    }
}
