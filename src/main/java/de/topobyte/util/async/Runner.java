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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sebastian Kuerten (sebastian@topobyte.de)
 */
class Runner extends Thread
{

	final static Logger logger = LoggerFactory.getLogger(Runner.class);

	private Executer executer;
	private BlockingQueue<Task> runnables;

	public Runner(Executer executer)
	{
		this.executer = executer;
		runnables = new ArrayBlockingQueue<>(1);
	}

	@Override
	public void run()
	{
		while (true) {
			try {
				Task task = runnables.take();
				if (task.getType() == TaskType.TASK_DO) {
					task.getRunnable().run();
					executer.reuseRunner(this);
				} else if (task.getType() == TaskType.TASK_FINISH) {
					// logger.debug("received finish");
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void runTask(Task task)
	{
		runnables.add(task);
	}

}
