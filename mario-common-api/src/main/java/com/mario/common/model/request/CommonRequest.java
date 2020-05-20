package com.mario.common.model.request;

import java.io.Serializable;

public class CommonRequest implements Serializable {

  private static final long serialVersionUID = -5747402125297698498L;
  private String currentUserId;
  private String initiationID;

  public CommonRequest() {
  }

  public Integer currentUserIdToInteger() throws NumberFormatException {
    return this.getCurrentUserId() == null ? null : Integer.parseInt(this.getCurrentUserId());
  }

  public Long currentUserIdToLong() throws NumberFormatException {
    return this.getCurrentUserId() == null ? null : Long.parseLong(this.getCurrentUserId());
  }

  public String getCurrentUserId() {
    return this.currentUserId;
  }

  public String getInitiationID() {
    return this.initiationID;
  }

  public void setCurrentUserId(String currentUserId) {
    this.currentUserId = currentUserId;
  }

  public void setInitiationID(String initiationID) {
    this.initiationID = initiationID;
  }

  @Override
  public String toString() {
    return "CommonRequest(currentUserId=" + this.getCurrentUserId() + ", initiationID=" + this
        .getInitiationID() + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof CommonRequest)) {
      return false;
    } else {
      CommonRequest other = (CommonRequest) o;
      if (!other.canEqual(this)) {
        return false;
      } else {
        Object this$currentUserId = this.getCurrentUserId();
        Object other$currentUserId = other.getCurrentUserId();
        if (this$currentUserId == null) {
          if (other$currentUserId != null) {
            return false;
          }
        } else if (!this$currentUserId.equals(other$currentUserId)) {
          return false;
        }

        Object this$initiationID = this.getInitiationID();
        Object other$initiationID = other.getInitiationID();
        if (this$initiationID == null) {
          if (other$initiationID != null) {
            return false;
          }
        } else if (!this$initiationID.equals(other$initiationID)) {
          return false;
        }

        return true;
      }
    }
  }

  protected boolean canEqual(Object other) {
    return other instanceof CommonRequest;
  }

  @Override
  public int hashCode() {
    int result = 1;
    Object $currentUserId = this.getCurrentUserId();
    result = result * 59 + ($currentUserId == null ? 43 : $currentUserId.hashCode());
    Object $initiationID = this.getInitiationID();
    result = result * 59 + ($initiationID == null ? 43 : $initiationID.hashCode());
    return result;
  }
}
