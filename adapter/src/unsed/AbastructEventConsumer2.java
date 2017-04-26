package unsed;

// package ec;
//
// import java.util.Queue;
// import java.util.Timer;
// import java.util.TimerTask;
// import java.util.concurrent.ConcurrentLinkedQueue;
//
// import org.apache.log4j.Logger;
//
// import v.V;
//
// public abstract class AbastructEventConsumer2<T> implements EventConsumer<T>
// {
//
// private static final Logger log = Logger
// .getLogger(AbastructEventConsumer2.class);
//
// private static final int TIMER_INTERVAL_TO_SLEEP = V.TIME_INTERVAL_TO_SLEEP;
//
// private static final int SLEEP_INTERVAL = V.SLEEP_INTERVAL;
//
// private volatile boolean isrun;
//
// private volatile boolean sleep;
//
// private final Timer timer = new Timer();
//
// private final Queue<T> events = new ConcurrentLinkedQueue<T>();
//
// private final Runnable eventsConsumer = new Runnable() {
// public void run() {
// TimerTask task = null;
// boolean taskCancelled = true;
// while (isrun) {
// try {
// if (!sleep) {
// T e = events.poll();
// if (e != null) {
// if (!taskCancelled) {
// task.cancel();
// taskCancelled = true;
// }
// consumer(e);
// } else {
// if (taskCancelled) {
// task = new TimerTask() {
// @Override
// public void run() {
// sleep = true;
// }
// };
// timer.schedule(task, TIMER_INTERVAL_TO_SLEEP);
// taskCancelled = false;
// }
// }
// } else {
// Thread.sleep(SLEEP_INTERVAL);
// }
// } catch (Exception e) {
// log.error("AEC CONSUMER ERROR", e);
// }
// }
// }
// };
//
// protected AbastructEventConsumer2() {
// isrun = false;
// }
//
// protected abstract void consumer(T e) throws Exception;
//
// @Override
// public void start() {
// isrun = true;
// new Thread(eventsConsumer).start();
// }
//
// @Override
// public void stop() {
// isrun = false;
// timer.cancel();
// }
//
// @Override
// public boolean isAlive() {
// return isrun;
// }
//
// @Override
// public final void add(T e) {
// if (sleep) {
// sleep = false;
// }
// events.add(e);
//
// }
// }
