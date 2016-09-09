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

/**
 * @author Sebastian Kuerten (sebastian@topobyte.de)
 */
enum TaskType {

	/**
	 * a task that shall be executed
	 */
	TASK_DO,

	/**
	 * a pseudo task used to trigger end of execution
	 */
	TASK_FINISH

}
