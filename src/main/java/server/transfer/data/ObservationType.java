package server.transfer.data;

public enum ObservationType {
	PARTICULATEMATTER_PM10("particulateMatter_PM10"),
	PARTICULATEMATTER_PM2P5("particulateMatter_PM2p5");
	
	private final String val;

    private ObservationType(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
