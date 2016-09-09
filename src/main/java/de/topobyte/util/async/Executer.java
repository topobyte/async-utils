// Copyright 2016 Sebastian Kuerten
//
// This file is part of async-utils.
//
// async-utils is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// async-utils is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with async-utils. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.util.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sebastian Kuerten (sebastian@topobyte.de)
 */
public class Executer implements Runnable
{

	final static Logger logger = LoggerFactory.getLogger(Executer.class);

	private BlockingQueue<Task> queue;
	private BlockingQueue<Runner> threads;
	private int nThreads;

	private List<Runner> runners;
	private Thread manager;

	private Object syncPaused = new Object();
	private boolean paused = false;

	/**
	 * Create a new executer with a blocking queue of size capacity and
	 * threadNumber threads in the thread-pool for execution.
	 * 
	 * Use queue to add Tasks and finish to let the executer die after
	 * everything has been executed. Use join to join to the time when
	 * everything's actually finished.
	 * 
	 * @param capacity
	 *            the number of places in the queue.
	 * @param threadNumber
	 *            the number of threads to use in the thread-pool.
	 */
	public Executer(int capacity, int threadNumber)
	{
		nThreads = threadNumber;
		queue = new ArrayBlockingQueue<>(capacity);
		threads = new ArrayBlockingQueue<>(threadNumber);
		runners = new ArrayList<>();

		for (int i = 0; i < threadNumber; i++) {
			Runner runner = new Runner(this);
			threads.add(runner);
			runners.add(runner);
			runner.start();
		}

		manager = new Thread(this);
		manager.start();
	}

	/**
	 * @param task
	 *            the task to queue
	 */
	public void queue(Runnable task)
	{
		while (true) {
			try {
				queue.put(new Task(TaskType.TASK_DO, task));
				break;
			} catch (InterruptedException e) {
				// continue
			}
		}
	}

	/**
	 * after a call of this, all threads are going to finish after their
	 * execution.
	 */
	public void finish()
	{
		for (int i = 0; i < nThreads; i++) {
			while (true) {
				try {
					queue.put(new Task(TaskType.TASK_FINISH, null));
					break;
				} catch (InterruptedException e) {
					// continue
				}
			}
		}
	}

	/**
	 * Cancel the execution. i.e. clear the queue of queued tasks and let each
	 * thread finish at the next possible point in time.
	 */
	public void cancel()
	{
		queue.clear();
		finish();
	}

	/**
	 * Pause / Resume the execution
	 */
	public void pauseOrResume()
	{
		synchronized (syncPaused) {
			paused = !paused;
			if (!paused) {
				syncPaused.notify();
			}
		}
	}

	/**
	 * Join on finished all tasks.
	 */
	public void join()
	{
		for (Runner runner : runners) {
			while (true) {
				try {
					runner.join();
					logger.debug("runner joined");
					break;
				} catch (InterruptedException e) {
					// continue
				}
			}
		}
		while (true) {
			try {
				manager.join();
				logger.debug("manager joined");
				break;
			} catch (InterruptedException e) {
				// continue
			}
		}
	}

	int endsDelivered = 0;

	@Override
	public void run()
	{
		while (true) {
			try {
				synchronized (syncPaused) {
					if (paused) {
						syncPaused.wait();
					}
				}
				Task task = queue.take();
				Runner runner = threads.take();
				runner.runTask(task);
				if (task.getType() == TaskType.TASK_FINISH) {
					endsDelivered++;
					if (endsDelivered == nThreads)
						return;
				}
			} catch (InterruptedException e) {
				// continue
			}
		}
	}

	/*
	 * used by Runner to put back to thread-pool
	 */
	void reuseRunner(Runner runner)
	{
		while (true) {
			try {
				threads.put(runner);
				break;
			} catch (InterruptedException e) {
				// continue
			}
		}
	}

}
