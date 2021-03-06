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

package com.adaptris.core.services.aggregator;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.OutputStream;
import java.util.Collection;

import com.adaptris.annotation.DisplayOrder;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.CoreConstants;
import com.adaptris.core.CoreException;
import com.adaptris.core.util.ExceptionHelper;
import com.adaptris.util.text.mime.MultiPartOutput;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * {@link MessageAggregator} implementation that creates a new mime part for each message that needs to be joined up.
 * <p>
 * The original pre-split document is ignored, the unique ID of the message is used as the Content-ID of the new multipart; the
 * payloads from the split messages are used to form the resulting multipart message. If the split message contains a specific
 * metadata key (as configured by {@link #setPartContentIdMetadataKey(String)}) then the corresponding value will be used as that
 * parts <code>Content-Id</code>. If the metadata key does not exist, or is not configured, then the split message's unique-id will
 * be used. If the same <code>Content-Id</code> is observed for multiple split messages then results are undefined. The most likely
 * situation is that parts will be lost and only one preserved.
 * </p>
 * <p>
 * As a result of this join operation, the message will be marked as MIME encoded using {@link com.adaptris.core.CoreConstants#MSG_MIME_ENCODED}
 * metadata.
 * </p>
 * </p>
 * 
 * @config ignore-original-mime-aggregator
 * @see CoreConstants#MSG_MIME_ENCODED
 * @author lchan
 * 
 */
@XStreamAlias("ignore-original-mime-aggregator")
@DisplayOrder(order = {"encoding", "overwriteMetadata", "partContentIdMetadataKey"})
public class IgnoreOriginalMimeAggregator extends MimeAggregator {

  @Override
  public void joinMessage(AdaptrisMessage original, Collection<AdaptrisMessage> messages) throws CoreException {
    OutputStream out = null;
    try {
      MultiPartOutput output = new MultiPartOutput(original.getUniqueId());
      for (AdaptrisMessage m : messages) {
        output.addPart(m.getPayload(), getEncoding(), getContentId(m));
        overwriteMetadata(m, original);
      }
      out = original.getOutputStream();
      output.writeTo(out);
      original.addMetadata(CoreConstants.MSG_MIME_ENCODED, Boolean.TRUE.toString());
    }
    catch (Exception e) {
      ExceptionHelper.rethrowCoreException(e);
    }
    finally {
      closeQuietly(out);
    }
  }
}
