/*
 * Copyright 2015 Adaptris Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.adaptris.core.services.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.ServiceException;

public interface JdbcStatementParameter {
  
  /**
   * Apply this statement parameter to the {@link PreparedStatement}.
   *
   * @param parameterIndex the index in the {@link PreparedStatement}
   * @param statement the {@link PreparedStatement}
   * @param msg the AdaptrisMessage
   * @throws SQLException on exception
   */
  void apply(int parameterIndex, PreparedStatement statement, AdaptrisMessage msg) throws SQLException, ServiceException;

  /**
   * Get the name of this parameter.
   * 
   * @return name.
   */
  String getName();

  /**
   * Make a copy of the statement parameter.
   * 
   * @return a copy (might be a clone, it might not).
   */
  JdbcStatementParameter makeCopy();
}
