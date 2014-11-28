package net.ixitxachitls.dma.values.enums;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Entries;
import net.ixitxachitls.dma.values.Parser;

/** The possible groups for a character. */
public enum Group implements Named, Proto<Entries.BaseCharacterProto.Group>
{
  /** A guest user without any special permissions. */
  GUEST("Guest", Entries.BaseCharacterProto.Group.GUEST),

  /** A normal user. */
  USER("User", Entries.BaseCharacterProto.Group.USER),

  /** The player in possession of the entry. */
  PLAYER("Player", Entries.BaseCharacterProto.Group.PLAYER),

  /** A DM (in any campaign). */
  DM("DM", Entries.BaseCharacterProto.Group.DM),

  /** An administrator. */
  ADMIN("Admin", Entries.BaseCharacterProto.Group.ADMIN);

  public static final Parser<Group> PARSER = new Parser<Group>(1) {
    @Override
    public Optional<Group> doParse(String inValue)
    {
      return Group.fromString(inValue);
    }
  };

  /** Create the group.
   *
   * @param inName  the name of the value
   * @param inProto the proto enum value
   */
  private Group(String inName, Entries.BaseCharacterProto.Group inProto)
  {
    m_name = inName;
    m_proto = inProto;
  }

  /** The name of the group. */
  private String m_name;

  /** The proto enum value. */
  private Entries.BaseCharacterProto.Group m_proto;

  /**
   * Check if a group allows a given group.
   *
   * @param  inGroup the group to check against
   *
   * @return true if the other group is less or equally restricted than the
   *         current one
   */
  public boolean allows(Group inGroup)
  {
    return this.ordinal() >= inGroup.ordinal();
  }

  @Override
  public String getName()
  {
    return m_name;
  }

  @Override
  public Entries.BaseCharacterProto.Group toProto()
  {
    return m_proto;
  }

  @Override
  public String toString()
  {
    return m_name;
  }

  /**
   * Get the group matching the given proto value.
   *
   * @param  inGroup the proto value to look for
   * @return the matched group (will throw exception if not found)
   */
  public static Group fromProto(Entries.BaseCharacterProto.Group inGroup)
  {
    for(Group group : values())
      if(group.m_proto == inGroup)
        return group;

    throw new IllegalStateException("invalid proto group: " + inGroup);
  }

  /**
   * All the possible names for the group.
   *
   * @return the possible names
   */
  public static List<String> names()
  {
    List<String> names = new ArrayList<>();

    for(Group group : values())
      names.add(group.getName());

    return names;
  }

  /**
   * Get the group matching the given text.
   */
  public static Optional<Group> fromString(String inText)
  {
    for(Group group : values())
      if(group.m_name.equalsIgnoreCase(inText))
        return Optional.of(group);

    return Optional.absent();
  }
}
