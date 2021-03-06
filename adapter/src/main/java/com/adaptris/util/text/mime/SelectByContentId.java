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

package com.adaptris.util.text.mime;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * {@link PartSelector} implementation that selects by the Content-Id header of the MimeBodyPart.
 * 
 * @config mime-select-by-content-id
 * 
 * @author lchan
 * @author $Author: lchan $
 */
@XStreamAlias("mime-select-by-content-id")
public class SelectByContentId implements PartSelector {

  @NotNull
  @NotBlank
  private String contentId;

  public SelectByContentId() {
  }

  public SelectByContentId(String s) {
    this();
    setContentId(s);
  }

  /**
   *
   * @see PartSelector#select(MultiPartInput)
   */
  @Override
  public MimeBodyPart select(MultiPartInput m) throws MessagingException {
    return m.getBodyPart(getContentId());
  }

  @Override
  public List<MimeBodyPart> select(MimeMultipart in) throws MessagingException {
    ArrayList<MimeBodyPart> list = new ArrayList<MimeBodyPart>();
    list.add((MimeBodyPart)in.getBodyPart(getContentId()));
    return list;
  }

  /**
   * @return the position
   */
  public String getContentId() {
    return contentId;
  }

  /**
   * The Content-Id of the MimeBodyPart to select.
   *
   * @param i the position to set, count
   */
  public void setContentId(String i) {
    contentId = i;
  }

}
