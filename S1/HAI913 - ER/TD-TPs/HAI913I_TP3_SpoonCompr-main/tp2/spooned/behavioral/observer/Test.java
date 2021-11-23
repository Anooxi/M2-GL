package behavioral.observer;
/**
 * a Test class for the Observer Design pattern.
 *
 * @author anonbnr
 */
public class Test {
    public static void main(String[] args) {
        StockGrabber stockGrabber = new StockGrabber();
        StockObserver observer1 = new StockObserver(stockGrabber);
        stockGrabber.setIBMStock(197.0);
        stockGrabber.setAppleStock(677.6);
        stockGrabber.setGoogleStock(676.4);
        StockObserver observer2 = new StockObserver(stockGrabber);
        stockGrabber.setIBMStock(197.0);
        stockGrabber.setAppleStock(677.6);
        stockGrabber.setGoogleStock(676.4);
        stockGrabber.unregister(observer1);
        stockGrabber.setIBMStock(197.0);
        stockGrabber.setAppleStock(677.6);
        stockGrabber.setGoogleStock(676.4);
    }
}