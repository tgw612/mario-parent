package com.mario.common.model.request;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PrimaryIdRequest extends CommonPrimaryKeyRequest<Long> implements Serializable {

  private static final long serialVersionUID = -7978906852152285946L;
}
