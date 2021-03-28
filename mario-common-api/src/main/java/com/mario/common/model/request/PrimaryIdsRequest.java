package com.mario.common.model.request;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PrimaryIdsRequest extends CommonMuliPrimaryKeyRequest<Long> implements Serializable {

  private static final long serialVersionUID = -542294272675370539L;
}
