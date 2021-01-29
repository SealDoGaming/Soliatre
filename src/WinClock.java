import java.util.TimerTask;

public class WinClock extends TimerTask {
	private Soliatre display;
	
	public WinClock(Soliatre display) {
		this.display=display;
	}
    @Override
    public void run() {
        display.addThrow();
    }

}
