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

import static org.apache.commons.lang.StringUtils.isBlank;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import com.adaptris.annotation.DisplayOrder;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.ServiceException;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * {@link java.sql.Time} extension to StatementParameter.
 * <p>
 * This ignores the query-class configuration, and always attempts to format the string into a java.sql.Time using the configured
 * date formatter; if {@code convert-null} is true, then empty/blank/whitespace only values will be substituted by
 * {@link System#currentTimeMillis()}.
 * </p>
 * 
 * @config jdbc-time-statement-parameter
 * 
 * @see StatementParameter
 */
@XStreamAlias("jdbc-time-statement-parameter")
@DisplayOrder(order = {"name", "queryString", "queryType", "dateFormat", "convertNull"})
public class TimeStatementParameter extends TimestampStatementParameter {

  public TimeStatementParameter() {
    super();
  }

  public TimeStatementParameter(String query, QueryType type, SimpleDateFormat format) {
    this(query, type, null, null, format);
  }

  public TimeStatementParameter(String query, QueryType type, Boolean nullConvert, String name, SimpleDateFormat format) {
    super(query, type, nullConvert, name, format);
  }

  

  @Override
  protected java.util.Date toDate(Object value) throws ServiceException {
    if (isBlank((String) value) && convertNull()) {
      return new java.sql.Time(System.currentTimeMillis());
    }
    else {
      try {
        return new java.sql.Time(getFormatter().parse((String) value).getTime());
      }
      catch (Exception e) {
        throw new ServiceException("Failed to convert input String [" + value + "] to type [java.sql.Time]", e);
      }
    }
  }
  
  @Override
  public void apply(int parameterIndex, PreparedStatement statement, AdaptrisMessage msg) throws SQLException, ServiceException {
    java.sql.Time ts = (java.sql.Time) this.toDate(getQueryValue(msg));
    log.trace("Setting argument {} to [{}]", parameterIndex, ts);
    statement.setTime(parameterIndex, ts);
  }

  @Override
  public TimeStatementParameter makeCopy() {
    return new TimeStatementParameter(getQueryString(), getQueryType(), getConvertNull(), getName(), getFormatter());
  }
}
