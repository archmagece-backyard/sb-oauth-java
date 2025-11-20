package org.scriptonbasestar.oauth.client.nobi.token;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.scriptonbasestar.oauth.client.TokenPack;
import org.scriptonbasestar.oauth.client.exception.OAuthParsingException;
import org.scriptonbasestar.oauth.client.util.SBSingleInstances;

import java.io.IOException;

/**
 * @author archmagece
 * @since 2016-10-26 19
 * <p>
 * facebook
 */
public class ParamStyleTokenExtractor<TOKEN extends TokenPack>
    implements TokenExtractor<TOKEN> {
  private final ObjectMapper mapper;
  private final TypeReference collectionType;

  public ParamStyleTokenExtractor(TypeReference collectionType) {
    this.mapper = SBSingleInstances.getObjectMapper();
    this.collectionType = collectionType;
  }

  public ParamStyleTokenExtractor(ObjectMapper mapper, TypeReference collectionType) {
    this.mapper = mapper;
    this.collectionType = collectionType;
  }

  @Override
  public TOKEN extract(String responseString) {
    try {
      return (TOKEN) mapper.readValue(responseString, collectionType);
    } catch (IOException e) {
      throw new OAuthParsingException("fail to parse json response", e);
    }
  }
}
