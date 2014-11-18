package net.ixitxachitls.dma.values.enums;

import com.google.protobuf.ProtocolMessageEnum;

/** The interface for all enumeration values that convert to protos. */
public interface Proto<P extends ProtocolMessageEnum>
{
  /**
   * Convert the enum value to its corresponding proto value.
   *
   * @return the converted proto value
   */
  public P toProto();
}
