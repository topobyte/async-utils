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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sebastian Kuerten (sebastian@topobyte.de)
 */
public class TestExecuter
{

	final static Logger logger = LoggerFactory.getLogger(TestExecuter.class);

	/**
	 * Simple Test
	 */
	public static void main(String[] args)
	{
		Executer executer = new Executer(8, 5);
		for (int i = 0; i < 20; i++) {
			final int n = i;
			executer.queue(new Runnable() {

				@Override
				public void run()
				{
					logger.debug("started: " + n);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						//
					}
					logger.debug("ended:   " + n);
				}
			});
			logger.debug("queued: " + n);
		}
		executer.finish();
		executer.join();
		logger.debug("after join");
	}

}
