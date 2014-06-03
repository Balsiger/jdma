/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
 * All rights reserved
 *
 * This file is part of Dungeon Master Assistant.
 *
 * Dungeon Master Assistant is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Dungeon Master Assistant is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dungeon Master Assistant; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *****************************************************************************/

//------------------------------------------------------------------ imports

package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto;
import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto.Attack.Mode;
import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto.Attack.Style;
import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto.Subtype;
import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto.Type;
import net.ixitxachitls.dma.proto.Values.ParametersProto;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.Damage;
import net.ixitxachitls.dma.values.Dice;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Group;
import net.ixitxachitls.dma.values.LongFormattedText;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Parameters;
import net.ixitxachitls.dma.values.Range;
import net.ixitxachitls.dma.values.Rational;
import net.ixitxachitls.dma.values.Reference;
import net.ixitxachitls.dma.values.Selection;
import net.ixitxachitls.dma.values.Size;
import net.ixitxachitls.dma.values.SizeModifier;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.Union;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.conditions.Condition;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the basic jDMA base monster.
 *
 * @file          BaseMonster.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class BaseMonster extends BaseEntry
{
  //----------------------------------------------------------------- nested

  //----- monster type -----------------------------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The possible monster types in the game. */
  @ParametersAreNonnullByDefault
  public enum MonsterType implements EnumSelection.Named,
    EnumSelection.Proto<BaseMonsterProto.Type>
  {
    /** Aberration. */
    ABERRATION("Aberration", BaseMonsterProto.Type.ABERRATION),

    /** Animal. */
    ANIMAL("Animal", BaseMonsterProto.Type.ANIMAL),

    /** Construct. */
    CONSTRUCT("Construct", BaseMonsterProto.Type.CONSTRUCT),

    /** Dragon. */
    DRAGON("Dragon", BaseMonsterProto.Type.DRAGON),

    /** Elemental. */
    ELEMENTAL("Elemental", BaseMonsterProto.Type.ELEMENTAL),

    /** Fey. */
    FEY("Fey", BaseMonsterProto.Type.FEY),

    /** Giant. */
    GIANT("Giant", BaseMonsterProto.Type.GIANT),

    /** Humanoid. */
    HUMANOID("Humanoid", BaseMonsterProto.Type.HUMANOID),

    /** Magical Beast. */
    MAGICAL_BEAST("Magical Beast", BaseMonsterProto.Type.MAGICAL_BEAST),

    /** Monstrous Humanoid. */
    MONSTROUS_HUMANOID("Monstrous Humanoid",
                       BaseMonsterProto.Type.MONSTROUS_HUMANOID),

    /** Ooze. */
    OOZE("Ooze", BaseMonsterProto.Type.OOZE),

    /** Outsider. */
    OUTSIDER("Outsider", BaseMonsterProto.Type.OUTSIDER),

    /** Plant. */
    PLANT("Plant", BaseMonsterProto.Type.PLANT),

    /** Undead. */
    UNDEAD("Undead", BaseMonsterProto.Type.UNDEAD),

    /** Vermin. */
    VERMIN("Vermin", BaseMonsterProto.Type.VERMIN);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseMonsterProto.Type m_proto;

    /**
     * Create the enum value.
     *
     * @param inName  the name of the value
     * @param inProto the proto enum value
     */
    private MonsterType(String inName, BaseMonsterProto.Type inProto)
    {
      m_name = constant("monster.type", inName);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public Type toProto()
    {
      return m_proto;
    }

    /**
     * Get the monster type corresponding to the given proto enum value.
     *
     * @param inProto the proto value to get for
     * @return the corresponding enum value
     */
    public static MonsterType fromProto(BaseMonsterProto.Type inProto)
    {
      for(MonsterType type : values())
        if(type.m_proto == inProto)
          return type;

      throw new IllegalArgumentException("cannot convert monster type:"
        + inProto);
    }
  }

  //........................................................................
  //----- monster subtype --------------------------------------------------

  /** The possible monster sub types in the game. */
  @ParametersAreNonnullByDefault
  public enum MonsterSubtype implements EnumSelection.Named,
    EnumSelection.Proto<BaseMonsterProto.Subtype>
  {
    /** None. */
    NONE("None", BaseMonsterProto.Subtype.NONE_SUBTYPE),

    /** Air. */
    AIR("Air", BaseMonsterProto.Subtype.AIR),

    /** Aquatic. */
    AQUATIC("Aquatic", BaseMonsterProto.Subtype.AQUATIC),

    /** Archon. */
    ARCHON("Archon", BaseMonsterProto.Subtype.ARCHON),

    /** Augmented. */
    AUGMENTED("Augmented", BaseMonsterProto.Subtype.AUGMENTED),

    /** Baatezu. */
    BAATEZU("Baatezu", BaseMonsterProto.Subtype.BAATEZU),

    /** Chaotic. */
    CHAOTIC("Chaotic", BaseMonsterProto.Subtype.CHAOTIC),

    /** Cold. */
    COLD("Cold", BaseMonsterProto.Subtype.COLD),

    /** Earth. */
    EARTH("Earth", BaseMonsterProto.Subtype.EARTH),

    /** Eladrin. */
    ELADRIN("Eladrin", BaseMonsterProto.Subtype.ELADRIN),

    /** Elf. */
    ELF("Elf", BaseMonsterProto.Subtype.ELF),

    /** Evil. */
    EVIL("Evil", BaseMonsterProto.Subtype.EVIL),

    /** Extraplanar. */
    EXTRAPLANAR("Extraplanar", BaseMonsterProto.Subtype.EXTRAPLANAR),

    /** Fire. */
    FIRE("Fire", BaseMonsterProto.Subtype.FIRE),

    /** Goblinoid. */
    GOBLINOID("Goblinoid", BaseMonsterProto.Subtype.GOBLINOID),

    /** Good. */
    GOOD("Good", BaseMonsterProto.Subtype.GOOD),

    /** Guardinal. */
    GUARDINAL("Guardinal", BaseMonsterProto.Subtype.GUARDINAL),

    /** Human. */
    HUMAN("Human", BaseMonsterProto.Subtype.HUMAN),

    /** Incorporeal. */
    INCORPOREAL("Incorporeal", BaseMonsterProto.Subtype.INCORPOREAL),

    /** Lawful. */
    LAWFUL("Lawful", BaseMonsterProto.Subtype.LAEFUL),

    /** Native. */
    NATIVE("Native", BaseMonsterProto.Subtype.NATIVE),

    /** Orc. */
    ORC("Orc", BaseMonsterProto.Subtype.ORC),

    /** Reptilian. */
    REPTILIAN("Reptilian", BaseMonsterProto.Subtype.REPTILIAN),

    /** Shapechanger. */
    SHAPECHANGER("Shapechanger", BaseMonsterProto.Subtype.SHAPECHANGER),

    /** Swarm. */
    SWARM("Swarm", BaseMonsterProto.Subtype.SWARM),

    /** Water. */
    WATER("Water", BaseMonsterProto.Subtype.WATER);

    /** The value's name. */
    private String m_name;

    /** The corresponding proto enum value. */
    private BaseMonsterProto.Subtype m_proto;

    /**
     * Create the enum value.
     *
     * @param inName the name of the value
     * @param inProto the corresponding proto value
     */
    private MonsterSubtype(String inName, BaseMonsterProto.Subtype inProto)
    {
      m_name = constant("monster.type", inName);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public Subtype toProto()
    {
      return m_proto;
    }

    /**
     * Get the subtype corresponding to the given proto value.
     *
     * @param inProto the proto value to get for
     * @return the corresponding subtype
     */
    public static MonsterSubtype fromProto(BaseMonsterProto.Subtype inProto)
    {
      for(MonsterSubtype type : values())
        if(type.m_proto == inProto)
          return type;

      throw new IllegalArgumentException("cannot convert monster subtype: "
                                         + inProto);
    }
  }

  //........................................................................
  //----- movement mode ----------------------------------------------------

  /** The possible movement modes in the game. */
  @ParametersAreNonnullByDefault
  public enum MovementMode implements EnumSelection.Named,
    EnumSelection.Proto<BaseMonsterProto.Speed.Mode>
  {
    /** Burrowing movement. */
    BURROW("Burrow", BaseMonsterProto.Speed.Mode.BURROW),

    /** Climbing. */
    CLIMB("Climb", BaseMonsterProto.Speed.Mode.CLIMB),

    /** Flying. */
    FLY("Fly", BaseMonsterProto.Speed.Mode.FLY),

    /** Swimming. */
    SWIM("Swim", BaseMonsterProto.Speed.Mode.SWIM),

    /** Running. */
    RUN("", BaseMonsterProto.Speed.Mode.RUN);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseMonsterProto.Speed.Mode m_proto;

    /**
     * Create the enum value.
     *
     * @param inName the name of the value
     * @param inProto the corresponding proto value
     */
    private MovementMode(String inName, BaseMonsterProto.Speed.Mode inProto)
    {
      m_name = constant("movement.mode", inName);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public BaseMonsterProto.Speed.Mode toProto()
    {
      return m_proto;
    }

    /**
     * Convert the proto value to the corresponding enum value.
     *
     * @param inProto the proto to convert
     * @return the corresponding enum value
     */
    public static MovementMode fromProto(BaseMonsterProto.Speed.Mode inProto)
    {
      for(MovementMode mode : values())
        if(mode.m_proto == inProto)
          return mode;

      throw new IllegalArgumentException("cannot convert movement mode: "
                                         + inProto);
    }
  }

  //........................................................................
  //----- maneuverability --------------------------------------------------

  /** The possible movement modes in the game. */
  @ParametersAreNonnullByDefault
  public enum Maneuverability implements EnumSelection.Named,
    EnumSelection.Proto<BaseMonsterProto.Speed.Maneuverability>
  {
    /** Perfect maneuverability. */
    PERFECT("Pefect", BaseMonsterProto.Speed.Maneuverability.PERFECT),

    /** Good maneuverability. */
    GOOD("Good", BaseMonsterProto.Speed.Maneuverability.GOOD),

    /** Average maneuverability. */
    AVERAGE("Average", BaseMonsterProto.Speed.Maneuverability.AVERAGE),

    /** Poor maneuverability. */
    POOR("Poor", BaseMonsterProto.Speed.Maneuverability.POOR),

    /** Clumsy maneuverability. */
    CLUMSY("Clumsy", BaseMonsterProto.Speed.Maneuverability.CLUMSY),

    /** Clumsy maneuverability. */
    NONE("", BaseMonsterProto.Speed.Maneuverability.NONE);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseMonsterProto.Speed.Maneuverability m_proto;

    /**
     * Create the enum value.
     *
     * @param inName the name of the value
     * @param inProto the proto value
     */
    private Maneuverability(String inName,
                            BaseMonsterProto.Speed.Maneuverability inProto)
    {
      m_name = constant("maneuverability", inName);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public BaseMonsterProto.Speed.Maneuverability toProto()
    {
      return m_proto;
    }

    /**
     * Convert the proto value into the corresponding enum value.
     *
     * @param inProto the proto value to convert
     * @return the corresponding enum value
     */
    public static Maneuverability
      fromProto(BaseMonsterProto.Speed.Maneuverability inProto)
    {
      for(Maneuverability maneuverability : values())
        if(maneuverability.m_proto == inProto)
          return maneuverability;

      throw new IllegalArgumentException("cannot convert maneuverability: "
                                         + inProto);
    }
  }

  //........................................................................
  //----- climate ----------------------------------------------------------

  /** The possible climates in the game. */
  @ParametersAreNonnullByDefault
  public enum Climate implements EnumSelection.Named,
    EnumSelection.Proto<BaseMonsterProto.Climate>
  {
    /** Warm climate. */
    WARM("Warm", BaseMonsterProto.Climate.WARM),

    /** Cold climate. */
    COLD("cold", BaseMonsterProto.Climate.COLD_CLIMATE),

    /** Any climate. */
    ANY("Any", BaseMonsterProto.Climate.ANY),

    /** Temparete climate. */
    TEMPERATE("Temperate", BaseMonsterProto.Climate.TEMPERATE);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseMonsterProto.Climate m_proto;

    /** Create the enum value.
     *
     * @param inName  the name of the value
     * @param inProto the proto enum value
     */
    private Climate(String inName, BaseMonsterProto.Climate inProto)
    {
      m_name = constant("climate", inName);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public net.ixitxachitls.dma.proto.Entries.BaseMonsterProto.Climate
      toProto()
    {
      return m_proto;
    }


    /**
     * Create a enum value from a given proto.
     *
     * @param inProto the proto to convert
     * @return the corresponding enum value
     */
    public static Climate fromProto(BaseMonsterProto.Climate inProto)
    {
      for(Climate climate : values())
        if(climate.m_proto == inProto)
          return climate;

      throw new IllegalArgumentException("cannot convert climate: "
                                         + inProto);
    }
  }

  //........................................................................
  //----- terrain ----------------------------------------------------------

  /** The possible terrains in the game. */
  @ParametersAreNonnullByDefault
  public enum Terrain implements EnumSelection.Named,
    EnumSelection.Proto<BaseMonsterProto.Terrain>
  {
    /** Forest terrain. */
    FOREST("Forest", BaseMonsterProto.Terrain.FOREST),

    /** Marsh terrain. */
    MARSH("Marsh", BaseMonsterProto.Terrain.MARSH),

    /** Hills terrain. */
    HILLS("Hills", BaseMonsterProto.Terrain.HILLS),

    /** Mountain terrain. */
    MOUNTAIN("Mountain", BaseMonsterProto.Terrain.MOUNTAIN),

    /** Desert terrain. */
    DESERT("Desert", BaseMonsterProto.Terrain.DESERT),

    /** Plains terrain. */
    PLAINS("Plains", BaseMonsterProto.Terrain.PLAINS),

    /** Aquatic terrain. */
    AQUATIC("Aquatic", BaseMonsterProto.Terrain.AQUATIC_TERRAIN),

    /** Underground terrain. */
    UNDERGROUND("Underground", BaseMonsterProto.Terrain.UNDERGROUND),

    /** Infernal Battlefield of Acheron terrain. */
    INFENRAL_BATTLEFIELD_OF_ACHERON
    ("Infernal Battlefield of Acheron",
     BaseMonsterProto.Terrain.INFERNAL_BATTLEFIELD_OF_ACHERON),

    /** Infinite Layers of the Abyss terrain. */
    INFINITE_LAYERS_OF_THE_ABYSS
    ("Infinite Layers of the Abyss",
     BaseMonsterProto.Terrain.INFINITE_LAYERS_OF_THE_ABYSS),

    /** Elemental Plane of Air. */
    ELEMENTAL_PLANE_OF_AIR("Elemental Plane of Air",
                           BaseMonsterProto.Terrain.ELEMENTAL_PLANE_OF_AIR),

    /** Elemental Plane of Earth. */
    ELEMENTAL_PLANE_OF_EARTH("Elemental Plane of Earth",
                             BaseMonsterProto.Terrain.ELEMENTAL_PLANE_OF_EARTH),

    /** Elemental Plane of Fire. */
    ELEMENTAL_PLANE_OF_FIRE("Elemental Plane of Fire",
                            BaseMonsterProto.Terrain.ELEMENTAL_PLANE_OF_FIRE),

    /** Elemental Plane of Water. */
    ELEMENTAL_PLANE_OF_WATER("Elemental Plane of Water",
                             BaseMonsterProto.Terrain.ELEMENTAL_PLANE_OF_WATER),

    /** Windswept dephts of pandemonium. */
    WINDSWEPT_DEPTHS_OF_PANDEMONIUM
    ("Windswept Depths of Pandemonium",
     BaseMonsterProto.Terrain.WINDSWEPT_DEPTHS_OF_PANDEMONIUM),

    /** Any terrain. */
    ANY("Any", BaseMonsterProto.Terrain.ANY_TERRAIN);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseMonsterProto.Terrain m_proto;

    /**
     * Create the enum value.
     *
     * @param inName the name of the value
     * @param inProto the proto enum value
     */
    private Terrain(String inName, BaseMonsterProto.Terrain inProto)
    {
      m_name = constant("terrain", inName);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public BaseMonsterProto.Terrain toProto()
    {
      return m_proto;
    }

    /**
     * Convert the proto to the corresponding enum value.
     *
     * @param inProto the proto to convert
     * @return the corresponding enum value
     */
    public static Terrain fromProto(BaseMonsterProto.Terrain inProto)
    {
      for(Terrain terrain : values())
        if(terrain.m_proto == inProto)
          return terrain;

      throw new IllegalArgumentException("cannot convert terrain: " + inProto);
    }
  }

  //........................................................................
  //----- organization -----------------------------------------------------

  /** The possible terrains in the game. */
  @ParametersAreNonnullByDefault
  public enum Organization implements EnumSelection.Named,
    EnumSelection.Proto<BaseMonsterProto.Organization.Type>
  {
    /** Any organization. */
    ANY("Any", BaseMonsterProto.Organization.Type.ANY),

    /** Band organization. */
    BAND("Band", BaseMonsterProto.Organization.Type.BAND),

    /** Brood organization. */
    BROOD("Brood", BaseMonsterProto.Organization.Type.BROOD),

    /** Colony organization. */
    COLONY("Colony", BaseMonsterProto.Organization.Type.COLONY),

    /** Covey organization. */
    COVEY("Covey", BaseMonsterProto.Organization.Type.COVEY),

    /** Flight organization. */
    FLIGHT("Flight", BaseMonsterProto.Organization.Type.FLIGHT),

    /** Flock organization. */
    FLOCK("Flock", BaseMonsterProto.Organization.Type.FLOCK),

    /** Gang organization. */
    GANG("Gang", BaseMonsterProto.Organization.Type.GANG),

    /** Herd organization. */
    HERD("Herd", BaseMonsterProto.Organization.Type.HERD),

    /** Infestation organization. */
    INFESTATION("Infestation", BaseMonsterProto.Organization.Type.INFESTATION),

    /** Nest organization. */
    NEST("Nest", BaseMonsterProto.Organization.Type.NEST),

    /** Pack organization. */
    PACK("Pack", BaseMonsterProto.Organization.Type.PACK),

    /** Pair organization. */
    PAIR("Pair", BaseMonsterProto.Organization.Type.PAIR),

    /** Patrol organization. */
    PATROL("Patrol", BaseMonsterProto.Organization.Type.PATROL),

    /** Slaver Brood organization. */
    SLAVER_BROOD("Slaver Brood",
                 BaseMonsterProto.Organization.Type.SLAVER_BROOD),

    /** Solitary organization. */
    SOLITARY("Solitary", BaseMonsterProto.Organization.Type.SOLITARY),

    /** Squad organization. */
    SQUAD("Qquad", BaseMonsterProto.Organization.Type.SQUAD),

    /** Storm organization. */
    STORM("Storm", BaseMonsterProto.Organization.Type.STORM),

    /** Swarm organization. */
    SWARM("Swarm", BaseMonsterProto.Organization.Type.SWARM),

    /** Tangle organization. */
    TANGLE("Tangle", BaseMonsterProto.Organization.Type.TANGLE),

    /** Troupe organization. */
    TROUPE("Troupe", BaseMonsterProto.Organization.Type.TROUPE);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseMonsterProto.Organization.Type m_proto;

    /**
     * Create the enum value.
     *
     * @param inName the name of the value
     * @param inProto the proto enum value
     */
    private Organization(String inName,
                         BaseMonsterProto.Organization.Type inProto)
    {
      m_name = constant("organization", inName);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public BaseMonsterProto.Organization.Type toProto()
    {
      return m_proto;
    }

    public static Organization
      fromProto(BaseMonsterProto.Organization.Type inProto)
    {
      for(Organization organization : values())
        if(organization.m_proto == inProto)
          return organization;

      throw new IllegalArgumentException("cannot convert organization: "
                                         + inProto);
    }
  }

  //........................................................................
  //----- attack style -----------------------------------------------------

  /** The possible attack styles in the game. */
  @ParametersAreNonnullByDefault
  public enum AttackStyle implements EnumSelection.Named,
    EnumSelection.Proto<BaseMonsterProto.Attack.Style>
  {
    /** A melee attack. */
    MELEE("melee", BaseMonsterProto.Attack.Style.MELEE),

    /** A ranged attack. */
    RANGED("ranged", BaseMonsterProto.Attack.Style.RANGED);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseMonsterProto.Attack.Style m_proto;

    /**
     * Create the name.
     *
     * @param inName       the name of the value
     * @param inProto      the proto enum value
     */
    private AttackStyle(String inName, BaseMonsterProto.Attack.Style inProto)
    {
      m_name = constant("attack.style", inName);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public Style toProto()
    {
      return m_proto;
    }

    /**
     * Convert the proto enum value to its enum value.
     *
     * @param inProto  the proto value to convert
     * @return         the corresponding enum value
     */
    public static AttackStyle fromProto(BaseMonsterProto.Attack.Style inProto)
    {
      for(AttackStyle style : values())
        if(style.m_proto == inProto)
          return style;

      throw new IllegalArgumentException("cannot convert attack style: "
                                         + inProto);
    }
  }

  //........................................................................
  //----- attack mode -----------------------------------------------------

  /** The possible attack styles in the game. */
  @ParametersAreNonnullByDefault
  public enum AttackMode implements EnumSelection.Named,
    EnumSelection.Proto<BaseMonsterProto.Attack.Mode>
  {
    /** A tentacle attack. */
    TENTACLE("Tentacle", false, BaseMonsterProto.Attack.Mode.TENTACLE),

    /** A claw attack. */
    CLAW("Claw", false, BaseMonsterProto.Attack.Mode.CLAW),

    /** A bite attack. */
    BITE("bite", false, BaseMonsterProto.Attack.Mode.BITE),

    /** A fist attack. */
    FIST("Fist", false, BaseMonsterProto.Attack.Mode.FIST),

    /** A quill attack. */
    QUILL("Quill", true, BaseMonsterProto.Attack.Mode.QUILL),

    /** A weapon attack. */
    WEAPON("Weapon", false, BaseMonsterProto.Attack.Mode.WEAPON),

    /** A touch attack. */
    TOUCH("Touch", true, BaseMonsterProto.Attack.Mode.TOUCH),

    /** An incorporeal touch attack. */
    INCORPOREAL_TOUCH("Incorporeal Touch", true,
                      BaseMonsterProto.Attack.Mode.INCORPOREAL_TOUCH),

    /** A slam attack. */
    SLAM("Slam", false, BaseMonsterProto.Attack.Mode.SLAM),

    /** A sting attack. */
    STING("Sting", false, BaseMonsterProto.Attack.Mode.STING),

    /** A swarm attack. */
    SWARM("Swarm", false, BaseMonsterProto.Attack.Mode.SWARM),

    /** A ray attack. */
    RAY("Ray", true, BaseMonsterProto.Attack.Mode.RAY),

    /** A hoof attack. */
    HOOF("Hoof", true, BaseMonsterProto.Attack.Mode.HOOF),

    /** A snakes attack. */
    SNAKES("Snakes", true, BaseMonsterProto.Attack.Mode.SNAKES),

    /** A web attack. */
    WEB("Web", true, BaseMonsterProto.Attack.Mode.WEB);

    /** The value's name. */
    private String m_name;

    /** Flag if to use dexterity when attacking. */
    private boolean m_dexterity;

    /** The proto enum value. */
    private BaseMonsterProto.Attack.Mode m_proto;

    /**
     * Create the name.
     *
     * @param inName       the name of the value
     * @param inDexterity  whether dexterity is used for the attack
     * @param inProto      the proto enum value
     */
    private AttackMode(String inName, boolean inDexterity,
                       BaseMonsterProto.Attack.Mode inProto)
    {
      m_name = constant("attack.mode", inName);
      m_dexterity = inDexterity;
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    /**
     * Check whether to use dexterity for attacking.
     *
     * @return true if using dexterity for attacking
     */
    public boolean useDexterity()
    {
      return m_dexterity;
    }

    @Override
    public Mode toProto()
    {
      return m_proto;
    }

    /**
     * Convert the proto value to an enum value.
     *
     * @param inProto  the proto value to convert
     * @return the corresponding enum value
     */
    public static AttackMode fromProto(BaseMonsterProto.Attack.Mode inProto)
    {
      for(AttackMode mode : values())
        if(mode.m_proto == inProto)
          return mode;

      throw new IllegalArgumentException("cannot convert attack mode: "
                                         + inProto);
    }
  }

  //........................................................................
  //----- treasure ---------------------------------------------------------

  /** The possible sizes in the game. */
  @ParametersAreNonnullByDefault
  public enum Treasure implements EnumSelection.Named,
    EnumSelection.Proto<BaseMonsterProto.Treasure>
  {
    /** No treasure at all. */
    NONE("none", 0, BaseMonsterProto.Treasure.NONE_TREASURE),

    /** Standard treasure. */
    STANDARD("standard", 1, BaseMonsterProto.Treasure.STANDARD),

    /** Double the standard treasure. */
    DOUBLE("double standard", 2, BaseMonsterProto.Treasure.DOUBLE),

    /** Triple the standard treasure. */
    TRIPLE("triple standard", 3, BaseMonsterProto.Treasure.TRIPLE),

    /** Quadruple the standard treasure. */
    QUADRUPLE("quadruple standard", 4, BaseMonsterProto.Treasure.QUADRUPLE);

    /** The value's name. */
    private String m_name;

    /** The multiplier for treasures. */
    private int m_multiplier;

    /** The proto enum value. */
    private BaseMonsterProto.Treasure m_proto;

    /**
     * Create the name.
     *
     * @param inName       the name of the value
     * @param inMultiplier how much treasure we get
     * @param inProto      the proto enum value
     */
    private Treasure(String inName, int inMultiplier,
                     BaseMonsterProto.Treasure inProto)
    {
      m_name = constant("skill.modifier", inName);
      m_multiplier = inMultiplier;
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    /**
     * Get the multiplier for this treasure type.
     *
     * @return the multiplier to use for computing treasure amounts
     */
    public int multiplier()
    {
      return m_multiplier;
    }

    @Override
    public BaseMonsterProto.Treasure toProto()
    {
      return m_proto;
    }

    /**
     * Get the treasure value associated with the given proto value.
     *
     * @param inProto the proto to convert
     * @return the converted treasure value
     */
    public static Treasure fromProto(BaseMonsterProto.Treasure inProto)
    {
      for(Treasure treasure : values())
        if(treasure.m_proto == inProto)
          return treasure;

      throw new IllegalArgumentException("cannot convert treasure: " + inProto);
    }
  }

  //........................................................................
  //----- alignment --------------------------------------------------------

  /** The possible sizes in the game. */
  @ParametersAreNonnullByDefault
  public enum Alignment implements EnumSelection.Named, EnumSelection.Short,
    EnumSelection.Proto<BaseMonsterProto.Alignment>
  {
    /** Lawful Evil. */
    LE("Lawful Evil", "LE", BaseMonsterProto.Alignment.LAEWFUL_EVIL),

    /** Lawful Neutral. */
    LN("Lawful Neutral", "LN", BaseMonsterProto.Alignment.LAWFUL_NEUTRAL),

    /** Lawful Good. */
    LG("Lawful Good", "LG", BaseMonsterProto.Alignment.LAWFUL_GOOD),

    /** Chaotic Evil. */
    CE("Chaotic Evil", "CE", BaseMonsterProto.Alignment.CHAOTIC_EVIL),

    /** Chaotic Neutral. */
    CN("Chaotic Neutral", "CN", BaseMonsterProto.Alignment.CHOATIC_NETURAL),

    /** Chaotic Good. */
    CG("Chaotic Good", "CG", BaseMonsterProto.Alignment.CHAOTIC_GOOD),

    /** Neutral Evil. */
    NE("Neutral Evil", "NE", BaseMonsterProto.Alignment.NEUTRAL_EVIL),

    /** True Neutral. */
    N("Neutral", "N", BaseMonsterProto.Alignment.TRUE_NEUTRAL),

    /** Neutral Good. */
    NG("Neutral Good", "NG", BaseMonsterProto.Alignment.NEUTRAL_GOOD),

    /** Any chaotic alignment. */
    ANY_CHAOTIC("Any Chaotic", "AC", BaseMonsterProto.Alignment.ANY_CHAOTIC),

    /** Any evil alignment. */
    ANY_EVIL("Any Evil", "AE", BaseMonsterProto.Alignment.ANY_EVIL),

    /** Any good alignment. */
    ANY_GOOD("Any Good", "AG", BaseMonsterProto.Alignment.ANY_GOOD),

    /** Any lawful alignment. */
    ANY_LAWFUL("Any Lawful", "AL", BaseMonsterProto.Alignment.ANY_LAWFUL),

    /** Any alignment. */
    ANY("Any", "A", BaseMonsterProto.Alignment.ANY_ALIGNMENT);

    /** The value's name. */
    private String m_name;

    /** The value's short name. */
    private String m_short;

    /** The proto enum value. */
    private BaseMonsterProto.Alignment m_proto;

    /**
     * Create the name.
     *
     * @param inName      the name of the value
     * @param inShort     the short name of the value
     * @param inProto     the proto value
     */
    private Alignment(String inName, String inShort,
                      BaseMonsterProto.Alignment inProto)
    {
      m_name = constant("alignment",       inName);
      m_short = constant("alignment.short", inShort);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public String getShort()
    {
      return m_short;
    }

    @Override
    public BaseMonsterProto.Alignment toProto()
    {
      return m_proto;
    }

    /**
     * Convert the proto to the corresponding enum value.
     *
     * @param inProto the proto to convert
     * @return the corresponding enum value
     */
    public static Alignment fromProto(BaseMonsterProto.Alignment inProto)
    {
      for(Alignment alignment : values())
        if(alignment.m_proto == inProto)
          return alignment;

      throw new IllegalArgumentException("cannot convert alignment: "
                                         + inProto);
    }
  }

  //........................................................................
  //----- alignment status -------------------------------------------------

  /** The possible alignment modifiers in the game. */
  @ParametersAreNonnullByDefault
  public enum AlignmentStatus implements EnumSelection.Named,
    EnumSelection.Proto<BaseMonsterProto.AlignmentStatus>
  {
    /** Always. */
    ALWAYS("Always", BaseMonsterProto.AlignmentStatus.ALWAYS),

    /** Usually. */
    USUALLY("Usually", BaseMonsterProto.AlignmentStatus.USUALLY),

    /** Often. */
    OFTEN("Often", BaseMonsterProto.AlignmentStatus.OFTEN);

    /** The value's name. */
    private String m_name;

    /** The proto value. */
    private BaseMonsterProto.AlignmentStatus m_proto;

    /**
     * Create the name.
     *
     * @param inName      the name of the value
     * @param inProto     the proto value
     */
    private AlignmentStatus(String inName,
                            BaseMonsterProto.AlignmentStatus inProto)
    {
      m_name = constant("alignment.status", inName);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public BaseMonsterProto.AlignmentStatus toProto()
    {
      return m_proto;
    }

    private static AlignmentStatus
      fromProto(BaseMonsterProto.AlignmentStatus inProto)
    {
      for(AlignmentStatus status : values())
        if(status.m_proto == inProto)
          return status;

      throw new IllegalArgumentException("cannot convert alignment status: "
                                         + inProto);
    }
  }

  //........................................................................
  //----- language ---------------------------------------------------------

  /** The possible sizes in the game. */
  @ParametersAreNonnullByDefault
  public enum Language implements EnumSelection.Named,
    EnumSelection.Proto<BaseMonsterProto.Language.Name>
  {
    /** Aboleth. */
    ABOLETH("Aboleth", BaseMonsterProto.Language.Name.ABOLETH),

    /** Abyssal. */
    ABYSSAL("Abyssal", BaseMonsterProto.Language.Name.ABYSSAL),

    /** Aquan. */
    AQUAN("Aquan", BaseMonsterProto.Language.Name.AQUAN),

    /** Auran. */
    AURAN("Auran", BaseMonsterProto.Language.Name.AURAN),

    /** Celestial. */
    CELESTIAL("Celestial", BaseMonsterProto.Language.Name.CELESTIAL),

    /** Common. */
    COMMON("Common", BaseMonsterProto.Language.Name.COMMON),

    /** Draconic. */
    DRACONIC("Draconic", BaseMonsterProto.Language.Name.DRACONIC),

    /** Drow Sign Language. */
    DROW_SIGN("Drow Sign", BaseMonsterProto.Language.Name.DROW_SIGN),

    /** Druidic. */
    DRUIDIC("Druidic", BaseMonsterProto.Language.Name.DRUIDIC),

    /** Dwarven. */
    DWARVEN("Dwarven", BaseMonsterProto.Language.Name.DWARVEN),

    /** Elven. */
    ELVEN("Elven", BaseMonsterProto.Language.Name.ELVEN),

    /** Giant. */
    GIANT("Giant", BaseMonsterProto.Language.Name.GIANT),

    /** Gnome. */
    GNOME("Gnome", BaseMonsterProto.Language.Name.GNOME),

    /** Goblin. */
    GOBLIN("Goblin", BaseMonsterProto.Language.Name.GOBLIN),

    /** Gnoll. */
    GNOLL("Gnoll", BaseMonsterProto.Language.Name.GNOLL),

    /** Halfling. */
    HALFLING("Halfling", BaseMonsterProto.Language.Name.HALFLING),

    /** Ignan. */
    IGNAN("Ignan", BaseMonsterProto.Language.Name.IGNAN),

    /** Infernal. */
    INFERNAL("Infernal", BaseMonsterProto.Language.Name.INFERNAL),

    /** Kuo-toa. */
    KUO_TOA("Kuo-toa", BaseMonsterProto.Language.Name.KUO_TOA),

    /** Orc. */
    ORC("Orc", BaseMonsterProto.Language.Name.ORC),

    /** Sylvan. */
    SYLVAN("Sylvan", BaseMonsterProto.Language.Name.SYLVAN),

    /** Terran. */
    TERRAN("Terran", BaseMonsterProto.Language.Name.TERRAN),

    /** Undercommon. */
    UNDERCOMMON("Undercommon", BaseMonsterProto.Language.Name.UNDERCOMMON),

    /** None. */
    NONE("-", BaseMonsterProto.Language.Name.NONE);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseMonsterProto.Language.Name m_proto;

    /**
     * Create the name.
     *
     * @param inName       the name of the value
     * @param inProto      the proto enum value
     */
    private Language(String inName, BaseMonsterProto.Language.Name inProto)
    {
      m_name = constant("language", inName);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public BaseMonsterProto.Language.Name toProto()
    {
      return m_proto;
    }

    /**
     * Convert the given proto value to the corresponding enum value.
     *
     * @param inProto the proto value to convert
     * @return the corresponding enum value
     */
    public static Language fromProto(BaseMonsterProto.Language.Name inProto)
    {
      for(Language language : values())
        if(language.m_proto == inProto)
          return language;

      throw new IllegalArgumentException("cannot convert language: " + inProto);
    }
  }

  //........................................................................
  //----- language modifier ------------------------------------------------

  /** The possible sizes in the game. */
  @ParametersAreNonnullByDefault
  public enum LanguageModifier implements EnumSelection.Named,
    EnumSelection.Proto<BaseMonsterProto.Language.Modifier>
  {
    /** Automatic. */
    AUTOMATIC("Automatic", BaseMonsterProto.Language.Modifier.AUTOMATIC),

    /** Bonus. */
    BONUS("Bonus", BaseMonsterProto.Language.Modifier.BONUS),

    /** Some. */
    SOME("Some", BaseMonsterProto.Language.Modifier.SOME),

    /** Understand. */
    UNDERSTAND("Understand", BaseMonsterProto.Language.Modifier.UNDERSTAND);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseMonsterProto.Language.Modifier m_proto;

    /**
     * Create the name.
     *
     * @param inName       the name of the value
     * @param inProto      the proto value
     */
    private LanguageModifier(String inName,
                             BaseMonsterProto.Language.Modifier inProto)
    {
      m_name = constant("language.modifier", inName);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public BaseMonsterProto.Language.Modifier toProto()
    {
      return m_proto;
    }

    /**
     * Convert a proto value to the enum value.
     *
     * @param inProto the proto value to convert
     * @return the corresponding enum value
     */
    public static LanguageModifier
      fromProto(BaseMonsterProto.Language.Modifier inProto)
    {
      for(LanguageModifier modifier : values())
        if(modifier.m_proto == inProto)
          return modifier;

      throw new IllegalArgumentException("cannot convert language modifier: "
                                         + inProto);
    }
  }

  //........................................................................
  //----- save ------------------------------------------------------------

  /** The possible sizes in the game. */
  @ParametersAreNonnullByDefault
  public enum Save implements EnumSelection.Named, EnumSelection.Short,
    EnumSelection.Proto<BaseMonsterProto.Save>
  {
    /** Fortitude. */
    FORTITUDE("Fortitude", "For", BaseMonsterProto.Save.FORTITUDE),

    /** Reflex. */
    REFLEX("Reflex", "Ref", BaseMonsterProto.Save.REFLEX),

    /** Wisdom. */
    WISDOM("Wisdom", "Wis", BaseMonsterProto.Save.WISDOM_SAVE);

    /** The value's name. */
    private String m_name;

    /** The value's short name. */
    private String m_short;

    /** The proto enum value. */
    private BaseMonsterProto.Save m_proto;

    /**
     * Create the name.
     *
     * @param inName       the name of the value
     * @param inShort      the short name of the value
     * @param inProto      the proto value
     */
    private Save(String inName, String inShort, BaseMonsterProto.Save inProto)
    {
      m_name = constant("save.name", inName);
      m_short = constant("save.short", inShort);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String getShort()
    {
      return m_short;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public BaseMonsterProto.Save toProto()
    {
      return m_proto;
    }

    public static Save fromProto(BaseMonsterProto.Save inProto)
    {
      for(Save save : values())
        if(save.m_proto == inProto)
          return save;

      throw new IllegalArgumentException("cannot convert save: " + inProto);
    }
  }

  //........................................................................
  //----- ability ---------------------------------------------------------

  /** The possible sizes in the game. */
  @ParametersAreNonnullByDefault
  public enum Ability implements EnumSelection.Named, EnumSelection.Short
  {
    /** Unknown.*/
    UNKNOWN("Unknown", "Unk", BaseMonsterProto.Ability.UNKNOWN),

    /** Strength. */
    STRENGTH("Strength", "Str", BaseMonsterProto.Ability.STRENGTH),

    /** Dexterity. */
    DEXTERITY("Dexterity", "Dex", BaseMonsterProto.Ability.DEXTERITY),

    /** Constitution. */
    CONSTITUTION("Constitution", "Con", BaseMonsterProto.Ability.CONSTITUTION),

    /** Intelligence. */
    INTELLIGENCE("Intelligence", "Int", BaseMonsterProto.Ability.INTELLIGENCE),

    /** Wisdom. */
    WISDOM("Wisdom", "Wis", BaseMonsterProto.Ability.WISDOM),

    /** Charisma. */
    CHARISMA("Charisma", "Cha", BaseMonsterProto.Ability.CHARISMA),

    /** No ability. */
    NONE("None", "-", BaseMonsterProto.Ability.NONE);

    /** The value's name. */
    private String m_name;

    /** The value's short name. */
    private String m_short;

    /** The proto enum value. */
    private BaseMonsterProto.Ability m_proto;

    /** Create the name.
     *
     * @param inName       the name of the value
     * @param inShort      the short name of the value
     * @param inProto      the proto enum value
     *
     */
    private Ability(String inName, String inShort,
                    BaseMonsterProto.Ability inProto)
    {
      m_name = constant("ability.name", inName);
      m_short = constant("ability.short", inShort);
      m_proto = inProto;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the short name of the value
     *
     */
    @Override
    public String getShort()
    {
      return m_short;
    }

    /** Get the save as string.
     *
     * @return the name of the value
     *
     */
    @Override
    public String toString()
    {
      return m_name;
    }

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseMonsterProto.Ability toProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto     the proto value to look for
     * @return the matched enum (will throw exception if not found)
     */
    public static Ability fromProto(BaseMonsterProto.Ability inProto)
    {
      for(Ability ability : values())
        if(ability.m_proto == inProto)
          return ability;

      throw new IllegalStateException("invalid proto ability: " + inProto);
    }

   /**
     * All the possible names for the layout.
     *
     * @return the possible names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();

      for(Ability ability : values())
        names.add(ability.getName());

      return names;
    }

    /**
     * Get the layout matching the given text.
     */
    public static Optional<Ability> fromString(String inText)
    {
      for(Ability ability : values())
        if(ability.m_name.equalsIgnoreCase(inText))
          return Optional.of(ability);

      return Optional.absent();
    }
  };

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //----------------------------- BaseMonster ------------------------------

  /**
    * This is the internal, default constructor for an undefined value.
    *
    */
  protected BaseMonster()
  {
    super(TYPE);
  }

  //........................................................................
  //----------------------------- BaseMonster ------------------------------

  /**
    * This is the normal constructor.
    *
    * @param       inName the name of the base item
    *
    */
  public BaseMonster(String inName)
  {
    super(inName, TYPE);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final BaseType<BaseMonster> TYPE =
    new BaseType<BaseMonster>(BaseMonster.class);

  //----- size -------------------------------------------------------------

  /** The monsters size. */
  @Key("size")
  protected Multiple m_size = new Multiple(new Multiple.Element []
    {
      new Multiple.Element
      (new EnumSelection<Size>(Size.class), false),
      new Multiple.Element
      (new EnumSelection<SizeModifier>(SizeModifier.class),
       true, " (", ")"),
    });

  static
  {
    addIndex(new Index(Index.Path.SIZES, "Sizes", TYPE));
  }

  //........................................................................
  //----- type -------------------------------------------------------------

  /** The monster type and subtype. */
  @Key("type")
  protected Multiple m_monsterType =
    new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new EnumSelection<MonsterType>(MonsterType.HUMANOID),
                           false),
      new Multiple.Element(new ValueList<EnumSelection<MonsterSubtype>>
                           (", ", new EnumSelection<MonsterSubtype>
                            (MonsterSubtype.HUMAN)), true, " (", ")"),
    });

  static
  {
    addIndex(new Index(Index.Path.TYPES, "Types", TYPE));
    addIndex(new Index(Index.Path.SUBTYPES, "Subtypes", TYPE));
  }

  //........................................................................
  //----- hit dice ---------------------------------------------------------

  /** The monster's hit dice. */
  @Key("hit dice")
  protected Dice m_hitDice = new Dice();

  // the indexes for number of dices and dice type
  static
  {
    addIndex(new Index(Index.Path.HDS, "HDs", TYPE));
    addIndex(new Index(Index.Path.DICES, "Dices", TYPE));
  }

  //........................................................................
  //----- speed ------------------------------------------------------------

  /** The monster's speed. */
  @Key("speed")
  protected ValueList<Multiple> m_speed =
    new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new EnumSelection<MovementMode>(MovementMode.class),
                           true),
      new Multiple.Element(new Distance(), false),
      new Multiple.Element(new EnumSelection<Maneuverability>
                           (Maneuverability.class), true, " (", ")"),
    }));

  static
  {
    addIndex(new Index(Index.Path.MOVEMENT_MODES, "Movement Modes", TYPE));
    addIndex(new Index(Index.Path.SPEEDS, "Speeds", TYPE));
    addIndex(new Index(Index.Path.MANEUVERABILITIES, "Maneuverabilitys", TYPE));
  }

  //........................................................................
  //----- natural armor ----------------------------------------------------

  /** The natural armor of the monster. */
  @Key("natural armor")
  protected Modifier m_natural =
    new Modifier(0, Modifier.Type.NATURAL_ARMOR)
    .withDefaultType(Modifier.Type.NATURAL_ARMOR);

  static
  {
    addIndex(new Index(Index.Path.NATURAL_ARMORS, "Natural Armors", TYPE));
  }

  //........................................................................
  //----- base attack ------------------------------------------------------

  /** The base attack bonus. */
  @Key("base attack")
  protected Number m_attack = new Number(-1, 100, true);

  static
  {
    addIndex(new Index(Index.Path.BASE_ATTACKS, "Base Attacks", TYPE));
  }

  //........................................................................
  //----- strength ---------------------------------------------------------

  /** The monster's Strength. */
  @Key("strength")
  protected Number m_strength = new Number(-1, 100, false);

  static
  {
    addIndex(new Index(Index.Path.STRENGTHS, "Strengths", TYPE));
  }

  //........................................................................
  //----- dexterity --------------------------------------------------------

  /** The monster's Dexterity. */
  @Key("dexterity")
  protected Number m_dexterity = new Number(-1, 100, false);

  static
  {
    addIndex(new Index(Index.Path.DEXTERITIES, "Dexterities", TYPE));
  }

  //........................................................................
  //----- constitution -----------------------------------------------------

  /** The monster's Constitution. */
  @Key("constitution")
  protected Number m_constitution = new Number(-1, 100, false);

  static
  {
    addIndex(new Index(Index.Path.CONSTITUTIONS, "Constitutions", TYPE));
  }

  //........................................................................
  //----- intelligence -----------------------------------------------------

  /** The monster's Intelligence. */
  @Key("intelligence")
  protected Number m_intelligence = new Number(-1, 100, false);

  static
  {
    addIndex(new Index(Index.Path.INTELLIGENCES, "Intelligences", TYPE));
  }

  //........................................................................
  //----- wisdom -----------------------------------------------------------

  /** The monster's Wisdom. */
  @Key("wisdom")
  protected Number m_wisdom = new Number(-1, 100, false);

  static
  {
    addIndex(new Index(Index.Path.WISDOMS, "Wisdoms", TYPE));
  }

  //........................................................................
  //----- charisma ---------------------------------------------------------

  /** The monster's Charisma. */
  @Key("charisma")
  protected Number m_charisma = new Number(-1, 100, false);

  static
  {
    addIndex(new Index(Index.Path.CHARISMAS, "Charismas", TYPE));
  }

  //........................................................................
  //----- fortitude save ---------------------------------------------------

  /** The monster's fortitude save. */
  @Key("fortitude save")
  protected Number m_fortitudeSave = new Number(-1, 100, true);

  static
  {
    addIndex(new Index(Index.Path.FORTITUDE_SAVES, "Fortitude Saves", TYPE));
  }

  //........................................................................
  //----- will save --------------------------------------------------------

  /** The monster's will save. */
  @Key("will save")
  protected Number m_willSave = new Number(-1, 100, true);

  static
  {
    addIndex(new Index(Index.Path.WILL_SAVES, "Will Saves", TYPE));
  }

  //........................................................................
  //----- reflex save ------------------------------------------------------

  /** The monster's reflex save. */
  @Key("reflex save")
  protected Number m_reflexSave = new Number(-1, 100, true);

  static
  {
    addIndex(new Index(Index.Path.REFLEX_SAVES, "Reflex Saves", TYPE));
  }

  //........................................................................
  //----- primary attacks --------------------------------------------------

  /** The monster's attacks. */
  @Key("primary attacks")
  protected ValueList<Multiple> m_primaryAttacks
    = new ValueList<Multiple>(",", new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new Dice().withEditType("name[dice]"), true),
      new Multiple.Element(new EnumSelection<AttackMode>(AttackMode.class),
                           false),
      new Multiple.Element(new EnumSelection<AttackStyle>(AttackStyle.class),
                           false),
      new Multiple.Element(new Damage().withEditType("name[damage]"),
                           false, " (", ")"),
    }));

  //........................................................................
  //----- secondary attacks ------------------------------------------------

  /** The monster's attacks. */
  @Key("secondary attacks")
  protected ValueList<Multiple> m_secondaryAttacks = m_primaryAttacks;

  //........................................................................
  //----- space ------------------------------------------------------------

  /** The formatter for space. */
  // protected static ValueFormatter<Distance> s_spaceFormatter =
  //   new LinkFormatter<Distance>("/index/spaces/");

  /** The grouping for space values. */
  protected static final Group<Distance, Long, String> s_spaceGrouping =
    new Group<Distance, Long, String>(new Group.Extractor<Distance, Long>()
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Long extract(Distance inValue)
        {
          if(inValue == null)
            throw new IllegalArgumentException("must have a distance here");

          return 10L * (long)inValue.getAsFeet().getValue();
        }
      }, new Long [] { 5L, 10L, 25L, 50L, 100L, 150L, 200L, 300L, },
      new String [] { "0.5 ft", "1 ft", "2.5 ft", "5 ft",
                      "10 ft", "15 ft", "20 ft", "30 ft", "Infinite", },
      "$undefined$");

  /** The space the monster occupies (computed). */
  @Key("space")
  protected Distance m_space = new Distance()
    .withGrouping(s_spaceGrouping);

  static
  {
    addIndex(new Index(Index.Path.SPACES, "Spaces", TYPE));
  }

  //........................................................................
  //----- reach ------------------------------------------------------------

  /** The formatter for space. */
  // protected static ValueFormatter<Distance> s_reachFormatter =
  //   new LinkFormatter<Distance>("/index/reaches/");

  /** The grouping for space values. */
  protected static final Group<Distance, Long, String> s_reachGrouping =
    new Group<Distance, Long, String>(new Group.Extractor<Distance, Long>()
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Long extract(Distance inValue)
        {
          if(inValue == null)
            throw new IllegalArgumentException("must have a distance here");

          return (long)inValue.getAsFeet().getValue();
        }
      }, new Long [] { 0L, 5L, 10L, 15L, 20L, 30L, },
      new String [] { "0 ft", "5 ft", "10 ft", "15 ft", "20 ft", "30 ft",
                      "Infinite", },
      "$undefined$");

  /** The reach of the monster. */
  @Key("reach")
  protected Distance m_reach = new Distance()
    .withGrouping(s_reachGrouping);

  static
  {
    addIndex(new Index(Index.Path.REACHES, "Reaches", TYPE));
  }

  //........................................................................
  //----- special attacks --------------------------------------------------

  /** The special attacks. */
  @Key("special attacks")
  @WithBases
  protected ValueList<Multiple> m_specialAttacks =
    new ValueList<Multiple>
    (", ",
     new Multiple(new Multiple.Element []
       {
         new Multiple.Element
         (new Reference<BaseQuality>(BaseQuality.TYPE)
          .withParameter("Range", new Distance(), Parameters.Type.MAX)
          .withParameter("Increment", new Distance(), Parameters.Type.MAX)
          .withParameter("Name", new Name(), Parameters.Type.UNIQUE)
          .withParameter("Summary", new Name(), Parameters.Type.ADD)
          .withParameter("Level", new Number(0, 100), Parameters.Type.ADD)
          .withParameter("SpellLevel", new Number(0, 100), Parameters.Type.ADD)
          .withParameter("Value", new Number(1, 100), Parameters.Type.ADD)
          .withParameter("Modifier", new Modifier(), Parameters.Type.ADD)
          .withParameter("Dice", new Dice(), Parameters.Type.ADD)
          .withParameter("Times", new Number(1, 100), Parameters.Type.ADD)
          .withParameter("Class", new EnumSelection<BaseSpell.SpellClass>
                         (BaseSpell.SpellClass.class), Parameters.Type.ADD)
          .withParameter("Ability", new Number(0, 100), Parameters.Type.MAX)
          .withParameter("Type", new Name(), Parameters.Type.UNIQUE)
          .withParameter("Duration", new Name(), Parameters.Type.ADD)
          .withParameter("Initial", new Name(), Parameters.Type.UNIQUE)
          .withParameter("Secondary", new Name(), Parameters.Type.UNIQUE)
          .withParameter("Damage", new Damage(), Parameters.Type.ADD)
          .withParameter("Incubation", new Name(), Parameters.Type.MIN)
          .withParameter("DC", new Number(1, 100), Parameters.Type.MAX)
          .withParameter("HP", new Number(1, 1000), Parameters.Type.MAX)
          .withParameter("Burst", new Number(1, 100), Parameters.Type.MAX)
          .withParameter("Str", new Number(-100, 100), Parameters.Type.ADD)
          .withParameter("Dex", new Number(-100, 100), Parameters.Type.ADD)
          .withParameter("Con", new Number(-100, 100), Parameters.Type.ADD)
          .withParameter("Wis", new Number(-100, 100), Parameters.Type.ADD)
          .withParameter("Int", new Number(-100, 100), Parameters.Type.ADD)
          .withParameter("Cha", new Number(-100, 100), Parameters.Type.ADD)
          .withTemplate("reference", "/quality/"), false),
         new Multiple.Element(new Number(1, 100)
                              .withEditType("name[per day]"), true, "/", null)
       }));

  //........................................................................
  //----- special qualities ------------------------------------------------

  /** The special qualities. */
  @Key("special qualities")
  @WithBases
  @SuppressWarnings("rawtypes") // raw condition
  protected ValueList<Multiple> m_specialQualities =
  new ValueList<Multiple>
  (", ",
   new Multiple(new Multiple.Element []
     {
       new Multiple.Element
       (new Reference<BaseQuality>(BaseQuality.TYPE)
        .withParameter("Range", new Distance(), Parameters.Type.MAX)
        .withParameter("Name", new Name(), Parameters.Type.UNIQUE)
        .withParameter("Summary", new Name(), Parameters.Type.ADD)
        .withParameter("Level", new Number(0, 100), Parameters.Type.ADD)
        .withParameter("SpellLevel", new Number(0, 100), Parameters.Type.ADD)
        .withParameter("Racial",
                       new Number(-50, 50, true), Parameters.Type.ADD)
        .withParameter("Value", new Number(0, 100), Parameters.Type.ADD)
        .withParameter("Modifier", new Modifier(), Parameters.Type.ADD)
        .withTemplate("reference", "/quality/"), false),
       new Multiple.Element(new Condition()
                            .withEditType("string[condition]"),
                            true, " if ", null),
       new Multiple.Element(new Number(1, 100)
                            .withEditType("name[per day]"), true, "/", null),
     }));


  //........................................................................
  //----- class skills -----------------------------------------------------

  /** The class skills. */
  @Key("class skills")
  protected ValueList<Multiple> m_classSkills =
    new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
      {
        new Multiple.Element
        (new Reference<BaseSkill>(BaseSkill.TYPE)
         .withParameter("Subtype",
                        new EnumSelection<BaseSkill.Subtype>
                        (BaseSkill.Subtype.class), Parameters.Type.UNIQUE),
         false),
        new Multiple.Element(new Modifier(0, Modifier.Type.GENERAL)
                             .withEditType("modifier[modifier]"),
                             false, ": ", null),
      }));

  //........................................................................
  //----- feats ------------------------------------------------------------

  /** The feats. */
  @Key("feats")
  protected ValueList<Reference<BaseFeat>> m_feats =
    new ValueList<Reference<BaseFeat>>
    (", ", new Reference<BaseFeat>(BaseFeat.TYPE)
     .withParameter("Name", new Name(), Parameters.Type.UNIQUE));

  //........................................................................
  //----- environment ------------------------------------------------------

  /** The environment. */
  @Key("environment")
  protected Multiple m_environment =
    new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new EnumSelection<Climate>(Climate.ANY), true),
      new Multiple.Element(new EnumSelection<Terrain>(Terrain.ANY), false),
    });

  static
  {
    addIndex(new Index(Index.Path.CLIMATES, "Climates", TYPE));
    addIndex(new Index(Index.Path.TERRAINS, "Terrains", TYPE));
  }

  //........................................................................
  //----- organization -----------------------------------------------------

  /** The monster's organization. */
  @Key("organization")
  protected ValueList<Multiple> m_organizations =
    new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
      {
        new Multiple.Element(new EnumSelection<Organization>
                             (Organization.class), false),
        new Multiple.Element(new Dice().withEditType("dice[number]"), true),
        new Multiple.Element(new ValueList<Multiple>
                             (", ",
                              new Multiple(new Multiple.Element []
                                {
                                  new Multiple.Element(new Dice(), false),
                                  new Multiple.Element(new Name(), false),
                                })),
                             true, " plus ", null),
      }));

  static
  {
    addIndex(new Index(Index.Path.ORGANIZATIONS, "Organizations", TYPE));
  }

  //........................................................................
  //----- challenge rating -------------------------------------------------

  /** The monsters challenge rating. */
  @Key("challenge rating")
  protected Rational m_cr = new Rational();

  static
  {
    addIndex(new Index(Index.Path.CRS, "CRs", TYPE));
  }

  //........................................................................
  //----- treasure ---------------------------------------------------------

  /** The monster's possible treasure. */
  @Key("treasure")
  protected EnumSelection<Treasure> m_treasure =
    new EnumSelection<Treasure>(Treasure.class);

  static
  {
    addIndex(new Index(Index.Path.TREASURES, "Treasures", TYPE));
  }

  //........................................................................
  //----- alignment --------------------------------------------------------

  /** The monster's alignment. */
  @Key("alignment")
  protected Multiple m_alignment =
    new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new EnumSelection<AlignmentStatus>
                           (AlignmentStatus.class), false),
      new Multiple.Element(new EnumSelection<Alignment>(Alignment.class),
                           false, " ", null),
    });

  static
  {
    addIndex(new Index(Index.Path.ALIGNMENTS, "Alignments", TYPE));
  }

  //........................................................................
  //----- advancements -----------------------------------------------------

  /** The monster's advancement. */
  @Key("advancements")
  protected ValueList<Multiple> m_advancements =
    new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
      {
        new Multiple.Element(new Range(1, 100), false),
        new Multiple.Element
        (new EnumSelection<Size>(Size.class), false,
         " HD (", ")"),
      }));

  //........................................................................
  //----- level adjustment -------------------------------------------------

  /** The monsters level adjustment. */
  @Key("level adjustment")
  protected Union m_levelAdjustment =
    new Union(new Selection(new String [] { "-" }),
              new Number(0, 20, true)).withEditType("name");

  static
  {
    addIndex(new Index(Index.Path.LEVEL_ADJUSTMENTS, "Level Adjustments",
                       TYPE));
  }

  //........................................................................
  //----- languages --------------------------------------------------------

  /** The monsters languages. */
  @Key("languages")
  protected ValueList<Multiple> m_languages =
    new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
      {
        new Multiple.Element(new EnumSelection<LanguageModifier>
                             (LanguageModifier.class), true),
        new Multiple.Element(new EnumSelection<Language>(Language.class),
                             false),
      }));

  static
  {
    addIndex(new Index(Index.Path.LANGUAGES, "Languages", TYPE));
  }

  //........................................................................
  //----- encounter --------------------------------------------------------

  /** The monsters encounter. */
  @Key("encounter")
  protected LongFormattedText m_encounter = new LongFormattedText();

  //........................................................................
  //----- combat -----------------------------------------------------------

  /** The monsters combat tactics. */
  @Key("combat")
  protected LongFormattedText m_combat = new LongFormattedText();

  //........................................................................
  //----- tactics ----------------------------------------------------------

  /** The monsters tactics. */
  @Key("tactics")
  protected LongFormattedText m_tactics = new LongFormattedText();

  //........................................................................
  //----- character --------------------------------------------------------

  /** The monsters character. */
  @Key("character")
  protected LongFormattedText m_character = new LongFormattedText();

  //........................................................................
  //----- reproduction -----------------------------------------------------

  /** The monsters reproduction. */
  @Key("reproduction")
  protected LongFormattedText m_reproduction =
    new LongFormattedText();

  //........................................................................
  //----- possessions ------------------------------------------------------

  /** The standard possessions. */
  @Key("possessions")
  protected ValueList<Multiple> m_possessions =
    new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
      {
        new Multiple.Element(new Name(), true),
        new Multiple.Element(new Text(), true),
      }));

  //........................................................................
  //----- good saves -------------------------------------------------------

  /** The good saving throws. */
  @Key("good saves")
  protected ValueList<EnumSelection<Save>> m_goodSaves =
    new ValueList<EnumSelection<Save>>(new EnumSelection<Save>(Save.class),
                                       ", ");
  //........................................................................

  /** The feats entries. */
  protected Set<BaseFeat> m_featEntries = new HashSet<BaseFeat>();

  static
  {
    extractVariables(BaseMonster.class);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getAttacks ------------------------------

  /**
   * Get the different attacks made by this monster.
   *
   * @return      an iterator with all the values
   *
   */
  // public Iterator<Multiple> getPrimaryAttacks()
  // {
  //   return m_primaryAttacks.iterator();
  // }

  //........................................................................
  //-------------------------------- getSize -------------------------------

  /**
   * Get the size of the monster.
   *
   * @return      the index in the size table.
   *
   */
  // @SuppressWarnings(value = "unchecked")
  // public BaseItem.Size getSize()
  // {
  //   return ((EnumSelection<BaseItem.Size>)
  //           m_size.get(0).get()).getSelected();
  // }

  //........................................................................
  //------------------------------- getReach -------------------------------

  /**
   * Get the monster's reach.
   *
   * @return      the monsters reach
   *
   */
  public Distance getReach()
  {
    return m_reach;
  }

  //........................................................................
  //-------------------------- getAbilityModifier --------------------------

  /**
   * Get the current modifier for the given ability.
   *
   * @param       inAbility the ability to get the modifier for
   *
   * @return      the ability modifier
   *
   */
  // public int getAbilityModifier(Global.Ability inAbility)
  // {
  //   if(inAbility == null)
  //     throw new IllegalArgumentException("must have an ability here");

  //   switch(inAbility)
  //   {
  //     case STRENGTH:

  //       return abilityMod(m_strength.get());

  //     case DEXTERITY:

  //       return abilityMod(m_dexterity.get());

  //     case CONSTITUTION:

  //       return abilityMod(m_constitution.get());

  //     case INTELLIGENCE:

  //       return abilityMod(m_intelligence.get());

  //     case WISDOM:

  //       return abilityMod(m_wisdom.get());

  //     case CHARISMA:

  //       return abilityMod(m_charisma.get());

  //     default:

  //       return 0;
  //   }
  // }

  //........................................................................
  //------------------------------- getMaxHP -------------------------------

  /**
   * Determine the maximally possible hit points (without any modifiers).
   *
   * @return      the maximally possible hit points
   *
   */
  // public int getMaxHP()
  // {
  //   return (int)m_hitDice.getMax();
  // }

  //........................................................................
  //------------------------------- getMinHP -------------------------------

  /**
   * Determine the minimally possible hit points (without any modifiers).
   *
   * @return      the minimally possible hit points
   *
   */
  // public int getMinHP()
  // {
  //   return (int)m_hitDice.getMin();
  // }

  //........................................................................
  //------------------------------- getFeats -------------------------------

  /**
   * Get the base feats of this monster.
   *
   * @return      an iterator over all base feats
   *
   */
  // public Iterator<BaseFeat> getFeats()
  // {
  //   return m_featEntries.iterator();
  // }

  //........................................................................
  //----------------------- collectSpecialQualities ------------------------

  /**
   * Get the special qualities for this and all base monsters.
   *
   * @return  a list of base qualities
   *
   */
  @SuppressWarnings("unchecked") // need to cast multiple part
  public List<Reference<BaseQuality>> collectSpecialQualities()
  {
    List<Reference<BaseQuality>> qualities = Lists.newArrayList();

    for(Multiple quality : m_specialQualities)
      qualities.add((Reference<BaseQuality>)quality.get(0));

    for(BaseEntry base : getBaseEntries())
    {
      if(!(base instanceof BaseMonster))
        continue;

      qualities.addAll(((BaseMonster)base).collectSpecialQualities());
    }

    return qualities;
  }

  //........................................................................
  //----------------------------- collectFeats -----------------------------

  /**
   * Collect the feats by entry.
   *
   * @param       ioFeats the feats store
   *
   */
  public void collectFeats(Multimap<Reference<BaseFeat>, String> ioFeats)
  {
    for(Reference<BaseFeat> feat : m_feats)
      ioFeats.put(feat, getName());

    for(BaseEntry base : getBaseEntries())
      if(base instanceof BaseMonster)
        ((BaseMonster)base).collectFeats(ioFeats);
  }

  //........................................................................
  //-------------------------------- collect -------------------------------

  @Override
  @SuppressWarnings("unchecked")
  protected <T extends Value<T>> void collect(String inName,
                                              Combined<T> ioCombined)
  {
    super.collect(inName, ioCombined);

    for(Multiple multiple : m_specialQualities)
    {
      // TODO: also use base values? See collectedSpecialQualities?
      Reference<BaseQuality> reference =
        (Reference<BaseQuality>)multiple.get(0);
      BaseQuality quality = reference.getEntry();

      if(quality == null)
        continue;

      Condition<?> condition = (Condition<?>)multiple.get(1);
      quality.collect(inName, ioCombined, null, reference.getParameters(),
                      condition.isDefined() ? condition : null);
    }

    for(Reference<BaseFeat> reference : m_feats)
    {
      BaseFeat feat = reference.getEntry();
      if(feat == null)
        continue;

      feat.collect(inName, ioCombined, reference.getParameters());
    }

    if("level".equals(inName))
    {
      if(m_hitDice.isDefined())
        ioCombined.addModifier(new Modifier(m_hitDice.getNumber()), this, null);
    }
  }

  //........................................................................

  //-------------------------------- level ---------------------------------

  /**
   * Get the level of the monster.
   *
   * @return      the monster's level or hit dice
   *
   */
  public int level()
  {
    return m_hitDice.getNumber();
  }

  //........................................................................
  //------------------------------ abilityMod ------------------------------

  /**
   * Get the ability modifier for the given value.
   *
   * @param       inAbility the ability to compute the modifier for
   *
   * @return      the ability modifier
   *
   */
  // public static int abilityMod(long inAbility)
  // {
  //   // if negative, we don't have the ability, thus a modifier of 0
  //   if(inAbility <= 0)
  //     return 0;

  //   return (int)(inAbility / 2) - 5;
  // }

  //........................................................................
  //----------------------------- skillPoints ------------------------------

  /**
   * Get the number of skill points of the monster.
   *
   * @return      the number of skill points
   *
   */
  // public int skillPoints()
  // {
  //   int type = ((Selection)m_monsterType.get(0).get()).getSelected();

  //   if(type < 0)
  //     type = 0;

  //   return (level() + 3)
  //     * Math.max(1,
  //              Global.SKILL_POINTS[type] + abilityMod(m_intelligence.get()));
  // }

  //........................................................................
  //----------------------------- skillRanks -------------------------------

  /**
   * Get the number of skill ranks in a specific skill.
   *
   * @param       inSkill the name of the skill to get the ranks of
   *
   * @return      the number of skill ranks
   *
   *
   */
  // public int skillRanks(String inSkill)
  // {
  //   for(Multiple skill : m_classSkills)
  //   {
  //     if(((SimpleText)skill.get(0).get()).get().equalsIgnoreCase(inSkill))
  //     {
  //       if(!skill.get(2).get().isDefined())
  //         return 0;

  //       return (int)((Number)skill.get(2).get()).get();
  //     }
  //   }

  //   return 0;
  // }

  //........................................................................

  //------------------------------ rollMaxHP -------------------------------

  /**
   * Roll the maximal hit points of the monster, including all modifiers.
   *
   * @return      the maximal hit point a monster of this type has
   *
   */
  // public int rollMaxHP()
  // {
  //   return m_hitDice.roll();
  // }

  //........................................................................

  //------------------------------ hasQuality ------------------------------

  /**
   * Determine if the monster has the given quality.
   *
   * @param       inQuality the quality to look for
   *
   * @return      true if the quality is there, false if not
   *
   */
  public boolean hasQuality(String inQuality)
  {
    for(Multiple value : m_specialQualities)
      if(value.toString().equalsIgnoreCase(inQuality))
        return true;

    for(Multiple value : m_specialAttacks)
      if(value.toString().equalsIgnoreCase(inQuality))
        return true;

    for(BaseEntry base : getBaseEntries())
      if(base instanceof BaseMonster)
        if(((BaseMonster)base).hasQuality(inQuality))
          return true;

    return false;
  }

  //........................................................................
  //-------------------------------- hasFeat -------------------------------

  /**
   * Determine if the monster has the given feat.
   *
   * @param       inFeat the feat to look for
   *
   * @return      true if the feat is there, false if not
   *
   */
  public boolean hasFeat(String inFeat)
  {
    for(Reference<BaseFeat> value : m_feats)
      if(value.toString().equalsIgnoreCase(inFeat))
        return true;

    for(BaseEntry base : getBaseEntries())
      if(base instanceof BaseMonster)
        if(((BaseMonster)base).hasFeat(inFeat))
          return true;

    return false;
  }

  //........................................................................
  //------------------------------ hasLanguage -----------------------------

  /**
   * Determine if the monster understands the given lanauge.
   *
   * @param       inLanguage the language to look for
   *
   * @return      true if the language is there, false if not
   *
   */
  // public boolean hasLanguage(String inLanguage)
  // {
  //   for(Multiple value : m_languages)
  //     if(value.toString().equalsIgnoreCase(inLanguage))
  //       return true;

  //   return false;
  // }

  //........................................................................
  //------------------------------ hasSubtype ------------------------------

  /**
   * Determine if the monster has the given subtype.
   *
   * @param       inType the type to look for
   *
   * @return      true if the language is there, false if not
   *
   */
  // @SuppressWarnings("unchecked") // casting for Multiple
  // public boolean hasSubtype(String inType)
  // {
  //   for(EnumSelection<MonsterType> value :
  //         (ValueList<EnumSelection<MonsterType>>)m_monsterType.get(1).get())
  //     if(value.toString().equalsIgnoreCase(inType))
  //       return true;

  //   return false;
  // }

  //........................................................................
  //--------------------------- hasMovementMode ----------------------------

  /**
   * Determine if the monster has the given movement mode.
   *
   * @param       inType the movement mode to look for
   *
   * @return      true if the language is there, false if not
   *
   */
  // public boolean hasMovementMode(String inType)
  // {
  //   for(Iterator<Multiple> i = m_speed.iterator(); i.hasNext(); )
  //     if(i.next().get(0).get().toString().equalsIgnoreCase(inType))
  //       return true;

  //   return false;
  // }

  //........................................................................
  //----------------------------- isClassSkill -----------------------------

  /**
   * Check if the given name is a describing a class skill of the monster.
   *
   * @param       inName the name of the skill to check
   *
   * @return      true if it is a class skill, false if not
   *
   */
  // public boolean isClassSkill(String inName)
  // {
  //   if(inName == null)
  //     return false;

  //   for(Iterator<Multiple> i = m_classSkills.iterator(); i.hasNext(); )
  //     if(i.next().get(0).get().toString().equalsIgnoreCase(inName))
  //       return true;

  //   return false;
  // }

  //........................................................................
  //--------------------------------- isDM ---------------------------------

  /**
   * Check whether the given user is the DM for this entry.
   *
   * @param       inUser the user accessing
   *
   * @return      true for DM, false for not
   *
   */
  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().hasAccess(BaseCharacter.Group.DM);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //---------------------------- checkQualities ----------------------------

  /**
   *
   * Check that the monster has the given qualities.
   *
   * @param       inQualities the qualities to check for
   *
   * @return      true if everything is ok, false if not
   *
   */
  // public boolean checkQualities(String ... inQualities)
  // {
  //   boolean result = true;

  //   for(String quality : inQualities)
  //     if(!hasQuality(quality))
  //     {
  //       result = false;
  //       addError(new CheckError("monster.quality",
  //                               "The should have the '" + quality
  //                               + "' quality."));
  //     }

  //   return result;
  // }

  //........................................................................
  //-------------------------- computeIndexValues --------------------------

  /**
   * Get all the values for all the indexes.
   *
   * @return      a multi map of values per index name
   *
   */
  @Override
  @SuppressWarnings("unchecked") // casting
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = super.computeIndexValues();

    values.put(Index.Path.SIZES, m_size.group());
    values.put(Index.Path.TYPES, m_monsterType.get(0).group());

    for(Value<?> value : ((ValueList<? extends Value<?>>)m_monsterType.get(1)))
      values.put(Index.Path.SUBTYPES, value.group());

    values.put(Index.Path.HDS, "" + m_hitDice.getNumber());
    values.put(Index.Path.DICES, "" + m_hitDice.getDice());

    for(Multiple value : m_speed)
    {
      values.put(Index.Path.MOVEMENT_MODES, value.get(0).group());
      values.put(Index.Path.SPEEDS, "" + value.get(1).group());
      values.put(Index.Path.MANEUVERABILITIES, "" + value.get(2).group());
    }

    values.put(Index.Path.NATURAL_ARMORS, m_natural.group());
    values.put(Index.Path.BASE_ATTACKS, m_attack.group());
    values.put(Index.Path.STRENGTHS, m_strength.group());
    values.put(Index.Path.DEXTERITIES, m_dexterity.group());
    values.put(Index.Path.CONSTITUTIONS, m_constitution.group());
    values.put(Index.Path.INTELLIGENCES, m_intelligence.group());
    values.put(Index.Path.WISDOMS, m_wisdom.group());
    values.put(Index.Path.CHARISMAS, m_charisma.group());
    values.put(Index.Path.SPACES, m_space.group());
    values.put(Index.Path.REACHES, m_reach.group());
    values.put(Index.Path.CLIMATES, m_environment.get(0).group());
    values.put(Index.Path.TERRAINS, m_environment.get(1).group());

    for(Multiple organization : m_organizations)
      values.put(Index.Path.ORGANIZATIONS, organization.get(0).group());

    values.put(Index.Path.CRS, m_cr.group());
    values.put(Index.Path.TREASURES, m_treasure.group());
    values.put(Index.Path.ALIGNMENTS, m_alignment.get(1).group());
    values.put(Index.Path.LEVEL_ADJUSTMENTS, m_levelAdjustment.group());

    for(Multiple language : m_languages)
      values.put(Index.Path.LANGUAGES, language.get(1).group());

    return values;
  }

  //........................................................................

  //-------------------------------- check ---------------------------------

  /**
   * Check the entry for possible problems.
   *
   * @param       inCampaign the campaign with all the data
   *
   * @return      false if a problem was found, true if not
   *
   */
  // public boolean check(CampaignData inCampaign)
  // {
  //   boolean result = true;

  //   // check some values because of the given type
  //   switch(((Selection)m_monsterType.get(0).get()).getSelected())
  //   {
  //     // Aberration
  //     // - Natural Weapon proficiency
  //     case 0:

  //       // - d8 hit dice
  //       if(m_hitDice.getDice() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d8"));
  //       }

  //       // - attack bonus 3/4 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() * 3 / 4)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() * 3 / 4)));
  //       }

  //       // - Darkvision 60 ft.
  //       result = checkQualities("Darkvision [Range 60 ft]");

  //       break;

  //     // Animal
  //     // - Natural Weapon proficiency (only)
  //     case 1:

  //       // - d8 hit dice
  //       if(m_hitDice.getDice() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d8"));
  //       }

  //       // - attack bonus 3/4 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() * 3 / 4)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() * 3 / 4)));
  //       }

  //       // - Low-light vision
  //       result = checkQualities("Low-Light Vision");

  //       // - Int 1 or 2
  //       if(m_intelligence.get() != 1 && m_intelligence.get() != 2)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.intelligence",
  //                                 "should have 1 or 2 (animal type)"));
  //       }

  //       // - Always neutral alignment
  //       if(((Selection)m_alignment.get(1).get()).getSelected() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.alignment",
  //                                 "should be neutral for animal"));
  //       }

  //       // - No treasure
  //       if(m_treasure.getSelected() != Treasure.NONE)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.treasure",
  //                                 "should not have treasure as animal"));
  //       }

  //       break;

  //     // Construct
  //     // - Proficient with no armor
  //     case 2:

  //       // - d10 hit dice
  //       if(m_hitDice.getDice() != 10)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d10"));
  //       }

  //       // - attack bonus 3/4 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() * 3 / 4)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() * 3 / 4)));
  //       }

  //       // - No Con
  //       if(m_constitution.get() >= 0)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.constitution",
  //                                 "should not have a constitution score "
  //                                 + "(construct type)"));
  //       }

  //       // - Darkvision 60 ft.
  //       // - Low-Light Vision
  //       // - Immunity mind affecting effects
  //       // - Immunity poison
  //       // - Immunity sleep effects
  //       // - Immunity paralysis
  //       // - Immunity stunning
  //       // - Immunity disease
  //       // - Immunity death effects
  //       // - Immunity necromancy effects
  //       // - Cannot heal damage on its own
  //       // - Not subject to critical hits
  //       // - Not subject to nonleathal damage
  //       // - Not subject to ability damage
  //       // - Not subject to ability drain
  //       // - Not subject to exhaustion
  //       // - Not subject to energy drain
  //       // - Immunity to any effect requiring Fort save
  //       // - Not subject to death from massive damage
  //       // - Cannot be raised
  //       // - Cannot be resurrected
  //       // - does not eat
  //       // - does not sleep
  //       // - does not breathe
  //       result = checkQualities("Darkvision [Range 60 ft]",
  //                               "Low-Light Vision",
  //                               "Immunity to Mind Affecting Effects",
  //                               "Immunity to Poison",
  //                               "Immunity to Sleep Effects",
  //                               "Immunity to Paralysis",
  //                               "Immunity to Stunning",
  //                               "Immunity to Disease",
  //                               "Immunity to Death Effects",
  //                               "Immunity to Necromancy Effects",
  //                               "No Natural Healing",
  //                               "Not Subject to Critical Hits",
  //                               "Not Subject to Nonlethal Damage",
  //                               "Not Subject to Ability Damage",
  //                               "Not Subject to Ability Drain",
  //                               "Not Subject to Exhaustion",
  //                               "Not Subject to Energy Drain",
  //                               "Immunity to Fortitude Save Effects",
  //                               "Not Subject to Death from Massive Damage",
  //                               "Cannot be Raised",
  //                               "Cannot be Resurrected",
  //                               "Does not Eat",
  //                               "Does not Sleep",
  //                               "Does not Breathe");

  //       break;

  //     // Dragon
  //     // - Proficient with no armor
  //     case 3:

  //       // - d12 hit dice
  //       if(m_hitDice.getDice() != 12)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d12"));
  //       }

  //       // - attack bonus total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber())
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber())));
  //       }

  //       // - Darkvision 60 ft.
  //       // - Low-light vision
  //       // - Immunity to magic sleep
  //       // - Immunity to paralysis
  //       result = checkQualities("Darkvision [Range 60 ft]",
  //                               "Low-Light Vision",
  //                               "Immunity to Magical Sleep",
  //                               "Immunity to Paralysis");
  //       break;

  //     // Elemental
  //     case 4:

  //       // - d8 hit dice
  //       if(m_hitDice.getDice() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d8"));
  //       }

  //       // - attack bonus 3/4 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() * 3 / 4)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() * 3 / 4)));
  //       }

  //       // - Darkvision 60 ft.
  //       // - Immunity to poison
  //       // - Immunity to sleep effects
  //       // - Immunity to paralysis
  //       // - Immunity to stunning
  //       // - Not subject to critical hits
  //       // - Not subject to flanking
  //       // - no soul
  //       // - does not eat, sleep, breathe
  //       result = checkQualities("Darkvision [Range 60 ft]",
  //                               "Immunity to Poison",
  //                               "Immunity to Sleep Effects",
  //                               "Immunity to Paralysis",
  //                               "Immunity to Stunning",
  //                               "Not Subject to Critical Hits",
  //                               "Not Subject to Flanking",
  //                               "No Soul",
  //                               "Does not Eat",
  //                               "Does not Sleep",
  //                               "Does not Breathe");

  //       break;

  //     // Fey
  //     // - Simple weapon proficiency
  //     case 5:

  //       // - d6 hit dice
  //       if(m_hitDice.getDice() != 6)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d6"));
  //       }

  //       // - attack bonus 1/2 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() / 2)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() / 2)));
  //       }

  //       // - Low-light vision
  //       if(!hasQuality("Low-Light Vision"))
  //       {
  //         result = false;
  //         addError(new CheckError("monster.quality",
  //                                 "The should have the 'Low-Light Vision' "
  //                                 + "quality."));
  //       }

  //       break;

  //     // Giant
  //     // - Simple weapon proficiency
  //     // - Martial weapon proficiency
  //     // - Natural weapon proficiency
  //     // - Shield proficiency of any armor proficiency
  //     case 6:

  //       // - d8 hit dice
  //       if(m_hitDice.getDice() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d8"));
  //       }

  //       // - attack bonus 3/4 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() * 3 / 4)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() * 3 / 4)));
  //       }

  //       // - Low-light vision
  //       if(!hasQuality("Low-Light Vision"))
  //       {
  //         result = false;
  //         addError(new CheckError("monster.quality",
  //                                 "The should have the 'Low-Light Vision' "
  //                                 + "quality."));
  //       }

  //       break;

  //     // Humanoid
  //     // - treated as Warriors if 1 Hit Dice
  //     // - Simple weapon proficiency
  //     // - Shield proficiency of any armor proficiency
  //     case 7:

  //       // - d8 hit dice
  //       if(m_hitDice.getDice() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d8"));
  //       }

  //       // - attack bonus 3/4 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() * 3 / 4)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() * 3 / 4)));
  //       }

  //       break;

  //     // Magical Beast
  //     // - natural weapon proficiency (only)
  //     // - No armor proficiency
  //     case 8:

  //       // - d10 hit dice
  //       if(m_hitDice.getDice() != 10)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d10"));
  //       }

  //       // - attack bonus total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber())
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber())));
  //       }

  //       // - Darkvision 60 ft.
  //       if(!hasQuality("Darkvision [Range 60 ft]"))
  //       {
  //         result = false;
  //         addError(new CheckError("monster.quality",
  //                                 "The should have the 'Darkvision 60 Ft' "
  //                                 + "quality."));
  //       }

  //       // - Int > 2
  //       if(m_intelligence.get() <= 2)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.intelligence",
  //                                 "should be more than 2 (magical beast "
  //                                 + "type)"));
  //       }

  //       break;

  //     // Monstrous Humanoid
  //     // - Simple proficiency
  //     case 9:

  //       // - d8 hit dice
  //       if(m_hitDice.getDice() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d8"));
  //       }

  //       // - attack bonus total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber())
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber())));
  //       }

  //       // - Darkvision 60 ft.
  //       if(!hasQuality("Darkvision [Range 60 ft]"))
  //       {
  //         result = false;
  //         addError(new CheckError("monster.quality",
  //                                 "The should have the 'Darkvision 60 Ft' "
  //                                 + "quality."));
  //       }

  //       break;

  //     // Ooze
  //     // - Natural weapon proficiency (only)
  //     // - No armor proficiency
  //     case 10:

  //       // - d10 hit dice
  //       if(m_hitDice.getDice() != 10)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d10"));
  //       }

  //       // - attack bonus 3/4 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() * 3 / 4)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() * 3 / 4)));
  //       }

  //       // - Mindless
  //       // - Blind
  //       // - Immunity poison
  //       // - Immunity sleep effects
  //       // - Immunity paralysis
  //       // - Immunity polymorph
  //       // - Immunity stunning
  //       // - Not subject to critical hits
  //       // - Not subject to flanking
  //       // - eats, breathes, does not sleep
  //       result = checkQualities("Mindless",
  //                               "Blind",
  //                               "Immunity to Poison",
  //                               "Immunity to Sleep Effects",
  //                               "Immunity to Paralysis",
  //                               "Immunity to Polymorph",
  //                               "Immunity to Stunning",
  //                               "Not Subject to Critical Hits",
  //                               "Not Subject to Flanking",
  //                               "Does not Sleep");

  //       break;

  //     // Outsider
  //     // - Simple weapon proficiency
  //     // - Martial weapon proficiency
  //     // - Shield proficiency if any armor proficiency
  //     case 11:

  //       // - d8 hit dice
  //       if(m_hitDice.getDice() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d8"));
  //       }

  //       // - attack bonus total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber())
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber())));
  //       }

  //       // - Darkvision 60 ft.
  //       // - No soul
  //       // - breathes, does not eat or sleep (unless native)
  //      result = checkQualities("Darkvision [Range 60 ft]",
  //                              "No Soul",
  //                              "Does not Eat",
  //                              "Does not Sleep");

  //       break;

  //     // Plant
  //     // - Natural weapon proficiency (only)
  //     // - No armor proficiency
  //     case 12:

  //       // - d8 hit dice
  //       if(m_hitDice.getDice() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d8"));
  //       }

  //       // - attack bonus 3/4 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() * 3 / 4)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() * 3 / 4)));
  //       }

  //       // - Low-light vision
  //       // - Immunity mind affecting effects
  //       // - Immunity poison
  //       // - Immunity sleep effects
  //       // - Immunity paralysis
  //       // - Immunity polymorph
  //       // - Immunity stunning
  //       // - Not subject to critical hits
  //       // - eats, breathes, does not sleep
  //       result = checkQualities("Low-Light Vision",
  //                               "Immunity to Mind Affecting Effects",
  //                               "Immunity to Poison",
  //                               "Immunity to Sleep Effects",
  //                               "Immunity to Paralysis",
  //                               "Immunity to Polymorph",
  //                               "Immunity to Stunning",
  //                               "Not Subject to Critical Hits",
  //                               "Does not Sleep");

  //       break;

  //     // Undead
  //     // - Natural weapon proficiency
  //     // - Simple weapon proficiency
  //     // - Shield proficiency if any armor proficiency
  //     case 13:

  //       // - d12 hit dice
  //       if(m_hitDice.getDice() != 12)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d12"));
  //       }

  //       // - attack bonus 1/2 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() / 2)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() / 2)));
  //       }

  //       // - Darkvision 60 ft.
  //       // - Immunity mind affecting effects
  //       // - Immunity poison
  //       // - Immunity sleep effects
  //       // - Immunity paralysis
  //       // - Immunity stunning
  //       // - Immunity disease
  //       // - Immunity death effects
  //       // - Not subject to critical hits
  //       // - Not subject to nonlethal damage
  //       // - Not subject to ability drain
  //       // - Not subject to energy drain
  //       // - Immunity to damage to Str, Dex, Con
  //       // - Immunity to fatigue effects
  //       // - Immunity to exhaustion effects
  //       // - Cannot heal on its own
  //       // - Immunity to effects requiring a Fort save
  //       // - Uses Cha for Concentration checks
  //       // - Not affected by raise dead, reincarnate
  //       // - does no eat, breath, sleep
  //       result = checkQualities("Darkvision [Range 60 ft]",
  //                               "Immunity to Mind Affecting Effects",
  //                               "Immunity to Poison",
  //                               "Immunity to Sleep Effects",
  //                               "Immunity to Paralysis",
  //                               "Immunity to Stunning",
  //                               "Immunity to Disease",
  //                               "Immunity to Death Effects",
  //                               "Not Subject to Critical Hits",
  //                               "Not Subject to Nonlethal Damage",
  //                               "Not Subject to Ability Drain",
  //                               "Not Subject to Energy Drain",
  //                               "Immunity to Strength Damage",
  //                               "Immunity to Dexterity Damage",
  //                               "Immunity to Constitution Damage",
  //                               "Immunity to Fatigue Effects",
  //                               "Immunity to Exhaustion Effects",
  //                               "No Natural Healing",
  //                               "Immunity to Fortitude Save Effects",
  //                               "Uses Charisma for Concentration checks",
  //                               "Not Affected by Raise Dead",
  //                               "Not Affected by Reincarnate",
  //                               "Does not Eat",
  //                               "Does not Breath",
  //                               "Does not Sleep");

  //       // - No Con
  //       if(m_constitution.get() >= 0)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.constitution",
  //                                 "should not have a constitution score "
  //                                 + "(undead type)"));
  //       }

  //       break;

  //     // Vermin
  //     // - Natural weapon proficiency (only)
  //     // - Shield proficiency if any armor proficiency
  //     case 14:

  //       // - d8 hit dice
  //       if(m_hitDice.getDice() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d8"));
  //       }

  //       // - attack bonus 3/4 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() * 3 / 4)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() * 3 / 4)));
  //       }

  //       // - Darkvision 60 ft.
  //       // - Mindless
  //       // - Immunity mind affecting effects
  //       result = checkQualities("Darkvision [Range 60 ft]",
  //                               "Mindless",
  //                               "Immunity to Mind Affecting Effects");

  //       // - No Int
  //       if(m_intelligence.get() >= 0)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.intelligence",
  //                                 "should not have an intelligence score "
  //                                 + "(vermin type)"));
  //       }

  //       break;

  //     default:

  //       addError(new CheckError("monster.type", "unknown type encountered"));
  //   }

  //   // check some values because of the given subtype
  //   if(hasSubtype("air"))
  //   {
  //     // - fly speed
  //     if(!hasMovementMode("fly"))
  //     {
  //       result = false;
  //       addError(new CheckError("monster.movement.mode.subtype",
  //                               "The monster does not have the fly speed "
  //                               + "required by its subtype"));
  //     }
  //   }

  //   if(hasSubtype("aquatic"))
  //   {
  //     // - swimm speed
  //     if(!hasMovementMode("swim"))
  //     {
  //       result = false;
  //       addError(new CheckError("monster.movement.mode.subtype",
  //                               "The monster does not have the swim speed "
  //                               + "required by its subtype"));
  //     }

  //     // - breathe underwater
  //     if(!hasQuality("breathe water"))
  //     {
  //       result = false;
  //       addError(new CheckError("monster.quality",
  //                               "aquatic subtype should have "
  //                               + "'breathe water'"));
  //     }

  //     // - cannot breathe air unless the amphibious special quality
  //     if(!hasQuality("amphibious") && !hasQuality("breathe no air"))
  //     {
  //       result = false;
  //       addError(new CheckError("monster.quality",
  //                               "aquatic subtype should have "
  //                               + "'breathe no air' (unless amphibious)"));
  //     }
  //   }

  //   if(hasSubtype("archon"))
  //   {
  //     // - Darkvision 60 ft.
  //     // - Low-light vision
  //     // - Aura of Menace
  //     // - Immunity electricity
  //     // - Immunity petrification
  //     // - +4 racial vs. poison
  //     // - Magic Circle Against Evil
  //     // - Teleport
  //     // - Tongues
  //     result = checkQualities("Darkvision [Range 60 ft]",
  //                             "Aura of Menace",
  //                             "Immunity to Electricity",
  //                             "Immunity to Petrification",
  //                             "Poison [Value +4 racial]",
  //                             "Magic Circle Against Evil",
  //                             "Teleport",
  //                             "Tongues");
  //   }

  //   if(hasSubtype("Augmented"))
  //   {
  //     // nothing to do here
  //   }

  //   if(hasSubtype("Baatezu"))
  //   {
  //     // - Immunity fire
  //     // - Immunity poison
  //     // - Resistance acid 10
  //     // - Resistance cold 10
  //     // - See in Darkness
  //     // - Summon
  //     // - Telepathy
  //     result = checkQualities("Immunity to Fire",
  //                             "Immunity to Poison",
  //                             "Resistance Acid [Value 10]",
  //                             "Resistance Cold [Value 10]",
  //                             "See in Darkness",
  //                             "Summon",
  //                             "Telepathy");
  //   }

  //   if(hasSubtype("Chaotic"))
  //   {
  //     // - affected by anything affecting chaotic alignment (even if
  //     //   different alignment)
  //     // - weapons treated as chaotic-aligned
  //     result = checkQualities("Affected as Chaotic",
  //                             "Weapons Chaotic");
  //   }

  //   if(hasSubtype("Cold"))
  //   {
  //     // - Immunity cold
  //     // - Vulnerability fire
  //     result = checkQualities("Immunity to Cold",
  //                             "Vulnerability Fire");
  //   }

  //   if(hasSubtype("Earth"))
  //   {
  //     // - burrow speed
  //     if(!hasMovementMode("burrow"))
  //     {
  //       result = false;
  //       addError(new CheckError("monster.movement.mode.subtype",
  //                               "The monster does not have the burrow "
  //                               + "speed required by its subtype"));
  //     }
  //   }

  //   if(hasSubtype("Eladrin"))
  //   {
  //     // - Darkvision 60 ft
  //     // - Low-light vision
  //     // - Immunity electricity
  //     // - Immunity petrification
  //     // - Resistance cold 10
  //     // - Resistance fire 10
  //     // - Tongues
  //     result = checkQualities("Darkvision [Range 60 ft]",
  //                             "Low-Light Vision",
  //                             "Immunity to Electricity",
  //                             "Immunity to Petrification",
  //                             "Resistance Cold [Value 10]",
  //                             "Resistance Fire [Value 10]",
  //                             "Tongues");
  //   }

  //   if(hasSubtype("Evil"))
  //   {
  //     // - affected by anything affecting evil alignment (even if
  //     //   different alignment)
  //     // - weapons treated as evil-aligned
  //     result = checkQualities("Affected as Evil",
  //                             "Weapons Evil");
  //   }

  //   if(hasSubtype("Extraplanar"))
  //   {
  //     // - can vary depending on where the monster is
  //   }

  //   if(hasSubtype("Fire"))
  //   {
  //     // - Immunity fire
  //     // - Vulnerability cold
  //     result = checkQualities("Immunity to Fire",
  //                             "Vulnerability Cold");
  //   }

  //   if(hasSubtype("Goblinoid"))
  //   {
  //     // - Goblin language
  //     if(!hasLanguage("Goblin"))
  //     {
  //       result = false;
  //       addError(new CheckError("monster.language",
  //                               "Goblinoid should have the Goblin "
  //                               + "language"));
  //     }
  //   }

  //   if(hasSubtype("Good"))
  //   {
  //     // - affected by anything affecting good alignment (even if
  //     //   different alignment)
  //     // - weapons treated as good-aligned
  //     result = checkQualities("Affected as Good",
  //                             "Weapons Good");
  //   }

  //   if(hasSubtype("Guardinal"))
  //   {
  //     // - Darkvision 60 ft
  //     // - Low-light vision
  //     // - Immunity electricity
  //     // - Immunity petrification
  //     // - Resistance cold 10
  //     // - Resistance fire 10
  //     // - Lay on Hands
  //     // - +4 racial vs. poison
  //     // - Speak with Animals
  //     result = checkQualities("Darkvision [Range 60 ft]",
  //                             "Low-Light Vision",
  //                             "Immunity to Electricity",
  //                             "Immunity to Petrification",
  //                             "Resistance Cold [Value 10]",
  //                             "Resistance Fire [Value 10]",
  //                             "Lay on Hands",
  //                             "Poison [Value +4 racial]",
  //                             "Speak with Animals");
  //   }

  //   if(hasSubtype("Incorporeal"))
  //   {
  //     // - Harmed only by incorporeal creatures, magic weapons, spells,
  //     //   spell-like abilities, supernatural abilities
  //     // - 50% chance to ignore damage from corporeal source (except
  //     //   positive energy, negative energy, force effects, ghost touch
  //     //   weapons)
  //     // - Can enter and pass through objects (not larger than self)
  //     // - Can sense creatures within 1 square (total concealment)
  //     // - Cannot pass through force effects
  //     // - Attacks ignore natural armor, armor, shields
  //     // - Operate in water as in air
  //     // - Cannot fall or take falling damage
  //     // - Cannot make trip attacks
  //     // - Cannot make grapple attacks
  //     // - Cannot be tripped
  //     // - Cannot be grappled
  //     // - Cannot manipulate physical objects
  //     // - Cannot be manipulated by physical objects
  //     // - Moves in complete silence
  //     // - No Strength (uses Dex for melee attacks)
  //     // - Scent ineffective
  //     // - Blindsight ineffective
  //     // - Can move at full speed even if cannot see
  //     result = checkQualities("Incorporeal");

  //     // - No natural armor
  //     if(m_natural.get() > 0)
  //     {
  //       result = false;
  //       addError(new CheckError("monster.armor",
  //                               "incorporeal monster don't have natural "
  //                               + "armor"));
  //     }
  //   }

  //   if(hasSubtype("Lawful"))
  //   {
  //     // - affected by anything affecting lawful alignment (even if
  //     //   different alignment)
  //     // - weapons treated as lawful-aligned
  //     result = checkQualities("Affected as Lawful",
  //                             "Weapons Lawful");
  //   }

  //   if(hasSubtype("Native"))
  //   {
  //       // - Native to Material Plane
  //       // - Can be raised, reincarnated or resurrected
  //       // - need to eat and sleep
  //   }

  //   if(hasSubtype("Reptilian"))
  //   {
  //     // - scaly
  //     // - cold-blooded
  //     result = checkQualities("Scaly",
  //                             "Cold-Blooded");
  //   }

  //   if(hasSubtype("Shapechanger"))
  //   {
  //     // - can assume one ore more alternate forms
  //     // - Natural weapon proficiency
  //     // - Simple weapon proficiency
  //     // - Shield proficiency if any armor proficiency
  //     result = checkQualities("Shapechanger");
  //   }

  //   if(hasSubtype("Swarm"))
  //   {
  //     // - collection of fine, diminutive, tiny creatures
  //     // - acts as single creature
  //     // - Can move through enemy squares
  //     // - Can move through cracks large enough for individual creatures
  //     // - 300 nonflying tiny creatures, or 1000 flying tiny creatures, or
  //     //   1500 nonflying diminutive creatures, or 5000 diminutive flying
  //     //   creatures, or 10000 fine creatures
  //     // - Not subject to critical hits
  //     // - Not subject to flanking
  //     // - Half damage from slashing and piercing weapons if tiny
  //     // - Immune to weapon damage if fine or diminutive
  //     // - Reducing it to 0 or less hit points causes it to break up
  //     // - Not subject to staggering
  //     // - Not subject to dying through damage
  //     // - Cannot be tripped
  //     // - Cannot be grappled
  //     // - Cannot be bull-rushed
  //     // - Cannot grapple
  //     // - Immunity to spells targeting a number of creatures
  //     // - Vulnerability area effects
  //     // - Diminutive and Fine swarms susceptible to high winds
  //     // - Deals automatic damage
  //     // - Does not threaten any squares
  //     // - Distraction

  //     result = checkQualities("Swarm");
  //   }

  //   if(hasSubtype("Tanar'ri"))
  //   {
  //     // - Immunity electricity
  //     // - Immunity poison
  //     // - Resistance acid 10
  //     // - Resistance cold 10
  //     // - Resistance fire 10
  //     // - Summon
  //     // - Telepathy
  //     result = checkQualities("Immunity Electricity",
  //                             "Immunity Poison",
  //                             "Resistance Acid [Value 10]",
  //                             "Resistance Cold [Value 10]",
  //                             "Resistance Fire [Value 10]",
  //                             "Summon",
  //                             "Telepathy");
  //   }

  //   if(hasSubtype("Water"))
  //   {
  //     // - need not make swim checks
  //     // - can breath water
  //     // - can breath air
  //     if(!hasMovementMode("swim"))
  //     {
  //       result = false;
  //       addError(new CheckError("monster.movement.mode.subtype",
  //                               "The monster does not have the swim speed "
  //                               + "required by its subtype"));
  //     }

  //     result = checkQualities("Does No Need Swim Checks",
  //                             "Breathe Water");
  //   }

  //   // check the number of skill points defined
  //   long total = 0;
  //   for(Iterator<Multiple> i = m_classSkills.iterator(); i.hasNext(); )
  //     total += ((Number)i.next().get(2).get()).get();

  //   if(total > skillPoints())
  //   {
  //     result = false;
  //     addError(new CheckError("monster.skill.points",
  //                           "uses " + total + " skill points, but only has "
  //                             + skillPoints()));
  //   }
  //   else
  //     if(total < skillPoints())
  //     {
  //       result = false;
  //       addError(new CheckError("monster.skill.points",
  //                               "uses only " + total
  //                             + " skill points from its " + skillPoints()));
  //     }

  //   return super.check() & result;
  // }

  //........................................................................
  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and make sure that all values are filled.
   *
   */
  // @SuppressWarnings(value = "unchecked")
  // public void complete()
  // {
  //   super.complete();

  //   BaseItem.Size size = getSize();

    //----- space ----------------------------------------------------------

    // // setup the space
    // if(size != null)
    //   m_space.setFeet(null, size.space(), null);

    // // swarm
    // if(hasSubtype("swarm"))
    //   m_space.setFeet(null, new Rational(10), null);

    //......................................................................
    //----- reach ----------------------------------------------------------

    // // setup the reach
    // if(size != null)
    //   m_reach.setFeet(null, new Rational
    //                 (getSize().reach(((EnumSelection<BaseItem.SizeModifier>)
    //                                     m_size.get(1).get()).getSelected())),
    //                   null);

    // // swarm
    // if(hasSubtype("swarm"))
    //   m_space.setFeet(null, new Rational(0), null);

    //......................................................................
    //----- base feats -----------------------------------------------------

    // for(Text text : m_feats)
    // {
    //   // get the base feat with this name
    //   BaseFeat entry =
    //     BaseCampaign.GLOBAL.getBaseEntry(text.get(), BaseFeat.TYPE);

    //   if(entry == null)
    //   {
    //     Log.warning("could not obtain feat '" + text.get()
    //                 + "' from campaign");

    //     continue;
    //   }

    //   m_featEntries.add(entry);
    // }

    //......................................................................
  // }

  //........................................................................
  //----------------------------- modifyValue ------------------------------

  /**
   * Modify the given value with information from the current attachment.
   *
   * @param       inType    the type of value to modify
   * @param       inValue   the value to modify, return in this object
   * @param       inDynamic a flag denoting if dynamic values are requested
   *
   * @return      the newly computed value (or null if no value to use)
   *
   */
//   public Modifier modifyValue(PropertyKey inType, Value inValue,
//                               boolean inDynamic)
//   {
//     if(inValue == null || !inValue.isDefined())
//       return null;

//     // temporary correction
//     if(inType == HIT_DICE)
//       return new Modifier(Modifier.Type.ADD, 40);

//     return super.modifyValue(inType, inValue, inDynamic);
//   }

  //........................................................................

  //------------------------------- groupHP --------------------------------

  /**
   * Make a grouping of the given HPs.
   *
   * @param       inHP the input value
   *
   * @return      the grouped value (as a String)
   *
   */
  // protected static String groupHP(long inHP)
  // {
  //   if(inHP <= 0)
  //     return "0000";

  //   if(inHP <= 5)
  //     return "0005";

  //   if(inHP <= 10)
  //     return "0010";

  //   if(inHP <= 15)
  //     return "0015";

  //   if(inHP <= 20)
  //     return "0020";

  //   if(inHP <= 25)
  //     return "0025";

  //   if(inHP <= 30)
  //     return "0030";

  //   if(inHP <= 40)
  //     return "0050";

  //   if(inHP <= 50)
  //     return "0050";

  //   if(inHP <= 100)
  //     return "0100";

  //   if(inHP <= 250)
  //     return "0250";

  //   if(inHP <= 500)
  //     return "0500";

  //   if(inHP <= 1000)
  //     return "1000";

  //   return "Infinite";
  // }

  //........................................................................

  @SuppressWarnings("unchecked")
  @Override
  public Message toProto()
  {
    BaseMonsterProto.Builder builder = BaseMonsterProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());

    if(m_size.isDefined())
    {
      builder.setSize(((EnumSelection<Size>)m_size.get(0))
                      .getSelected().toProto());
      builder.setSizeModifier(((EnumSelection<SizeModifier>)
                              m_size.get(1)).getSelected().toProto());
    }

    if(m_monsterType.isDefined())
    {
      builder.setType(((EnumSelection<MonsterType>)m_monsterType.get(0))
                      .getSelected().toProto());
      for(EnumSelection<MonsterSubtype> subtype
        : (ValueList<EnumSelection<MonsterSubtype>>)m_monsterType.get(1))
        builder.addSubtype(subtype.getSelected().toProto());
    }

    if(m_hitDice.isDefined())
      builder.setHitDice(m_hitDice.toProto());

    if(m_speed.isDefined())
      for(Multiple speed : m_speed)
      {
        BaseMonsterProto.Speed.Builder speedBuilder =
          BaseMonsterProto.Speed.newBuilder();

        if(speed.get(0).isDefined())
          speedBuilder.setMode(((EnumSelection<MovementMode>)speed.get(0))
                               .getSelected().toProto());

        speedBuilder.setDistance(((Distance)speed.get(1)).toProto());

        if(speed.get(2).isDefined())
          speedBuilder.setManeuverability(((EnumSelection<Maneuverability>)
                                          speed.get(2))
                                          .getSelected().toProto());

        builder.addSpeed(speedBuilder.build());
      }

    if(m_natural.isDefined())
      builder.setNaturalArmor(m_natural.toProto());

    if(m_attack.isDefined())
      builder.setBaseAttack((int)m_attack.get());

    if(m_strength.isDefined())
      builder.setStrength((int)m_strength.get());

    if(m_dexterity.isDefined())
      builder.setDexterity((int)m_dexterity.get());

    if(m_constitution.isDefined())
      builder.setConstitution((int)m_constitution.get());

    if(m_wisdom.isDefined())
      builder.setWisdom((int)m_wisdom.get());

    if(m_intelligence.isDefined())
      builder.setIntelligence((int)m_intelligence.get());

    if(m_charisma.isDefined())
      builder.setCharisma((int)m_charisma.get());

    if(m_fortitudeSave.isDefined())
      builder.setFortitudeSave((int)m_fortitudeSave.get());

    if(m_willSave.isDefined())
      builder.setWillSave((int)m_willSave.get());

    if(m_reflexSave.isDefined())
      builder.setReflexSave((int)m_reflexSave.get());

    if(m_primaryAttacks.isDefined())
      for(Multiple attack : m_primaryAttacks)
      {
        BaseMonsterProto.Attack.Builder attackBuilder =
          BaseMonsterProto.Attack.newBuilder();

        if(attack.get(0).isDefined())
          attackBuilder.setAttacks(((Dice)attack.get(0)).toProto());

        if(attack.get(1).isDefined())
          attackBuilder.setMode(((EnumSelection<AttackMode>)attack.get(1))
                                .getSelected().toProto());

        if(attack.get(2).isDefined())
          attackBuilder.setStyle(((EnumSelection<AttackStyle>)attack.get(2))
                                 .getSelected().toProto());

        if(attack.get(3).isDefined())
          attackBuilder.setDamage(((Damage)attack.get(3)).toProto());

        builder.addPrimaryAttack(attackBuilder.build());
      }

    if(m_secondaryAttacks.isDefined())
      for(Multiple attack : m_secondaryAttacks)
      {
        BaseMonsterProto.Attack.Builder attackBuilder =
          BaseMonsterProto.Attack.newBuilder();

        if(attack.get(0).isDefined())
          attackBuilder.setAttacks(((Dice)attack.get(0)).toProto());

        if(attack.get(1).isDefined())
          attackBuilder.setMode(((EnumSelection<AttackMode>)attack.get(1))
                                .getSelected().toProto());

        if(attack.get(2).isDefined())
          attackBuilder.setStyle(((EnumSelection<AttackStyle>)attack.get(2))
                                 .getSelected().toProto());

        if(attack.get(3).isDefined())
          attackBuilder.setDamage(((Damage)attack.get(3)).toProto());

        builder.addSecondaryAttack(attackBuilder.build());
      }

    if(m_space.isDefined())
      builder.setSpace(m_space.toProto());

    if(m_reach.isDefined())
      builder.setReach(m_reach.toProto());

    if(m_specialAttacks.isDefined())
      for(Multiple special : m_specialAttacks)
      {
        BaseMonsterProto.QualityReference.Builder reference =
          BaseMonsterProto.QualityReference.newBuilder();

        Reference<BaseQuality> ref = (Reference<BaseQuality>)special.get(0);
        reference.setReference
        (BaseMonsterProto.Reference.newBuilder()
         .setName(ref.getName())
         .setParameters(ref.getParameters() != null
         ? ParametersProto.getDefaultInstance()
         : ref.getParameters().toProto())
         .build());

        if(special.get(1).isDefined())
          reference.setPerDay((int)((Number)special.get(1)).get());

        builder.addSpecialAttack(reference.build());
      }

    if(m_specialQualities.isDefined())
      for(Multiple special : m_specialQualities)
      {
        BaseMonsterProto.QualityReference.Builder reference =
          BaseMonsterProto.QualityReference.newBuilder();

        Reference<BaseQuality> ref = (Reference<BaseQuality>)special.get(0);
        reference.setReference
        (BaseMonsterProto.Reference.newBuilder()
         .setName(ref.getName())
         .setParameters(ref.getParameters() != null
         ? ParametersProto.getDefaultInstance()
         : ref.getParameters().toProto())
         .build());

        if(special.get(1).isDefined())
          reference.setCondition(((Condition)special.get(1)).getDescription());
        if(special.get(2).isDefined())
          reference.setPerDay((int)((Number)special.get(2)).get());

        builder.addSpecialQuality(reference.build());
      }

    if(m_classSkills.isDefined())
      for(Multiple skill : m_classSkills)
      {
        BaseMonsterProto.SkillReference.Builder reference =
          BaseMonsterProto.SkillReference.newBuilder();

        Reference<BaseSkill> ref = (Reference<BaseSkill>)skill.get(0);
        reference.setReference
        (BaseMonsterProto.Reference.newBuilder()
         .setName(ref.getName())
         .setParameters(ref.getParameters() != null
         ? ParametersProto.getDefaultInstance()
         : ref.getParameters().toProto())
         .build());

        if(skill.get(1).isDefined())
          reference.setModifier(((Modifier)skill.get(1)).toProto());

        builder.addClassSkill(reference);
      }

    if(m_feats.isDefined())
      for(Reference<BaseFeat> feat : m_feats)
      {
        BaseMonsterProto.Reference.Builder reference =
          BaseMonsterProto.Reference.newBuilder();

        reference.setName(feat.getName());
        if(feat.getParameters() != null)
          reference.setParameters(feat.getParameters().toProto());

        builder.addFeat(reference);
      }

    if(m_environment.get(0).isDefined())
      builder.setClimate(((EnumSelection<Climate>)m_environment.get(0))
                         .getSelected().toProto());

    if(m_environment.get(1).isDefined())
      builder.setTerrain(((EnumSelection<Terrain>)m_environment.get(1))
                         .getSelected().toProto());

    if(m_organizations.isDefined())
      for(Multiple organization : m_organizations)
      {
        BaseMonsterProto.Organization.Builder org =
          BaseMonsterProto.Organization.newBuilder();

        if(organization.get(0).isDefined())
          org.setType(((EnumSelection<Organization>)organization.get(0))
                      .getSelected().toProto());
        if(organization.get(1).isDefined())
          org.setNumber(((Dice)organization.get(1)).toProto());
        if(organization.get(2).isDefined())
          for(Multiple plus : (ValueList<Multiple>)organization.get(2))
            org.addPlus(BaseMonsterProto.Organization.Plus.newBuilder()
                        .setNumber(((Dice)plus.get(0)).toProto())
                        .setText(((Name)plus.get(1)).get())
                        .build());

        builder.addOrganization(org.build());
      }

    if(m_cr.isDefined())
      builder.setChallengeRating(m_cr.toProto());

    if(m_treasure.isDefined())
      builder.setTreasure(m_treasure.getSelected().toProto());

    if(m_alignment.isDefined() && m_alignment.get(0).isDefined())
      builder.setAlignmentStatus(((EnumSelection<AlignmentStatus>)
                                 m_alignment.get(0)).getSelected().toProto());
    if(m_alignment.isDefined() && m_alignment.get(1).isDefined())
      builder.setAlignment(((EnumSelection<Alignment>)m_alignment.get(1))
                           .getSelected().toProto());

    if(m_advancements.isDefined())
      for(Multiple advancement : m_advancements)
        builder.addAdvancement
          (BaseMonsterProto.Advancement.newBuilder()
           .setRange(((Range)advancement.get(0)).toProto())
           .setSize(((EnumSelection<Size>)advancement.get(1))
                    .getSelected().toProto())
           .build());

    if(m_levelAdjustment.isDefined())
      if(m_levelAdjustment.getIndex() == 0)
        builder.setLevelAdjustment(0);
      else
        builder.setLevelAdjustment((int)((Number)m_levelAdjustment.get())
                                   .get());

    if(m_languages.isDefined())
      for(Multiple language : m_languages)
      {
        BaseMonsterProto.Language.Builder lang =
          BaseMonsterProto.Language.newBuilder();
        lang.setName(((EnumSelection<Language>)language.get(1))
                     .getSelected().toProto());
        if(language.get(0).isDefined())
          lang.setModifier(((EnumSelection<LanguageModifier>)language.get(0))
                           .getSelected().toProto());

        builder.addLanguage(lang.build());
      }

    if(m_encounter.isDefined())
      builder.setEncounter(m_encounter.get());

    if(m_combat.isDefined())
      builder.setCombat(m_combat.get());

    if(m_tactics.isDefined())
      builder.setTactics(m_tactics.get());

    if(m_character.isDefined())
      builder.setCharacter(m_character.get());

    if(m_reproduction.isDefined())
      builder.setReproduction(m_reproduction.get());

    if(m_possessions.isDefined())
      for(Multiple possession : m_possessions)
      {
        if(!possession.isDefined() ||
          (!possession.get(0).isDefined() && !possession.get(1).isDefined()))
          continue;

        BaseMonsterProto.Possession.Builder pos =
          BaseMonsterProto.Possession.newBuilder();

        if(possession.get(0).isDefined())
          pos.setName(((Name)possession.get(0)).get());
        if(possession.get(1).isDefined())
          pos.setText(((Text)possession.get(1)).get());

        builder.addPossession(pos.build());
      }

    if(m_goodSaves.isDefined())
      for(EnumSelection<Save> save : m_goodSaves)
        builder.addGoodSave(save.getSelected().toProto());

    BaseMonsterProto proto = builder.build();
    return proto;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseMonsterProto))
    {
      Log.warning("cannot parse proto " + inProto.getClass());
      return;
    }

    BaseMonsterProto proto = (BaseMonsterProto)inProto;

    if(proto.hasSize() && proto.hasSizeModifier())
      m_size =
        m_size.as(((EnumSelection<Size>)m_size.get(0))
                  .as(Size.fromProto(proto.getSize())),
                  ((EnumSelection<SizeModifier>)m_size.get(1))
                  .as(SizeModifier
                      .fromProto(proto.getSizeModifier())));

    if(proto.hasType())
    {
      List<EnumSelection<MonsterSubtype>> subtypes = new ArrayList<>();
      for(BaseMonsterProto.Subtype subtype : proto.getSubtypeList())
        subtypes.add(((ValueList<EnumSelection<MonsterSubtype>>)
                     m_monsterType.get(1)).createElement()
                     .as(MonsterSubtype.fromProto(subtype)));

      m_monsterType =
        m_monsterType.as(((EnumSelection<MonsterType>)m_monsterType.get(0))
                         .as(MonsterType.fromProto(proto.getType())),
                         ((ValueList<EnumSelection<MonsterSubtype>>)
                           m_monsterType.get(1))
                           .as(subtypes));
    }

    if(proto.hasHitDice())
      m_hitDice = m_hitDice.fromProto(proto.getHitDice());

    if(proto.getSpeedCount() > 0)
    {
      List<Multiple> speeds = new ArrayList<>();
      for(BaseMonsterProto.Speed speed : proto.getSpeedList())
      {
        Multiple multiple = m_speed.createElement();
        multiple = multiple.as(speed.hasMode()
                               ? ((EnumSelection<MovementMode>)multiple.get(0))
                                 .as(MovementMode.fromProto(speed.getMode()))
                               : multiple.get(0),
                               ((Distance)multiple.get(1))
                               .fromProto(speed.getDistance()),
                               speed.hasManeuverability()
                               ? ((EnumSelection<Maneuverability>)
                                 multiple.get(2))
                                 .as(Maneuverability
                                     .fromProto(speed.getManeuverability()))
                               : multiple.get(2));
        speeds.add(multiple);
      }

      m_speed = m_speed.as(speeds);
    }

    if(proto.hasNaturalArmor())
      m_natural = m_natural.fromProto(proto.getNaturalArmor());

    if(proto.hasBaseAttack())
      m_attack = m_attack.as(proto.getBaseAttack());

    if(proto.hasStrength())
      m_strength = m_strength.as(proto.getStrength());

    if(proto.hasDexterity())
      m_dexterity = m_dexterity.as(proto.getDexterity());

    if(proto.hasConstitution())
      m_constitution = m_constitution.as(proto.getConstitution());

    if(proto.hasIntelligence())
      m_intelligence = m_intelligence.as(proto.getIntelligence());

    if(proto.hasWisdom())
      m_wisdom = m_wisdom.as(proto.getWisdom());

    if(proto.hasCharisma())
      m_charisma = m_charisma.as(proto.getCharisma());

    if(proto.hasFortitudeSave())
      m_fortitudeSave = m_fortitudeSave.as(proto.getFortitudeSave());

    if(proto.hasWillSave())
      m_willSave = m_willSave.as(proto.getWillSave());

    if(proto.hasReflexSave())
      m_reflexSave = m_reflexSave.as(proto.getReflexSave());

    if(proto.getPrimaryAttackCount() > 0)
    {
      List<Multiple> attacks = new ArrayList<>();
      Multiple multiple = m_primaryAttacks.createElement();

      for(BaseMonsterProto.Attack attack : proto.getPrimaryAttackList())
      {
        multiple =
          multiple.as(attack.hasAttacks()
                      ? ((Dice)multiple.get(0)).fromProto(attack.getAttacks())
                      : multiple.get(0),
                      attack.hasMode()
                      ? ((EnumSelection<AttackMode>)multiple.get(1))
                        .as(AttackMode.fromProto(attack.getMode()))
                      : multiple.get(1),
                      attack.hasStyle()
                      ? ((EnumSelection<AttackStyle>)multiple.get(2))
                        .as(AttackStyle.fromProto(attack.getStyle()))
                      : multiple.get(2),
                      attack.hasDamage()
                      ? ((Damage)multiple.get(3)).fromProto(attack.getDamage())
                      : multiple.get(3));
        attacks.add(multiple);
      }

      m_primaryAttacks = m_primaryAttacks.as(attacks);
    }

    if(proto.getSecondaryAttackCount() > 0)
    {
      List<Multiple> attacks = new ArrayList<>();
      Multiple multiple = m_secondaryAttacks.createElement();

      for(BaseMonsterProto.Attack attack : proto.getSecondaryAttackList())
      {
        multiple =
          multiple.as(attack.hasAttacks()
                      ? ((Dice)multiple.get(0)).fromProto(attack.getAttacks())
                      : multiple.get(0),
                      attack.hasMode()
                      ? ((EnumSelection<AttackMode>)multiple.get(1))
                        .as(AttackMode.fromProto(attack.getMode()))
                      : multiple.get(1),
                      attack.hasStyle()
                      ? ((EnumSelection<AttackStyle>)multiple.get(2))
                        .as(AttackStyle.fromProto(attack.getStyle()))
                      : multiple.get(2),
                      attack.hasDamage()
                      ? ((Damage)multiple.get(3)).fromProto(attack.getDamage())
                      : multiple.get(3));
        attacks.add(multiple);
      }

      m_secondaryAttacks = m_secondaryAttacks.as(attacks);
    }

    if(proto.hasSpace())
      m_space = m_space.fromProto(proto.getSpace());

    if(proto.hasReach())
      m_reach = m_reach.fromProto(proto.getReach());

    if(proto.getSpecialAttackCount() > 0)
    {
      List<Multiple> references = new ArrayList<>();
      for(BaseMonsterProto.QualityReference reference
        : proto.getSpecialAttackList())
      {
        Multiple multiple = m_specialAttacks.createElement();

        Reference<BaseQuality> ref = (Reference<BaseQuality>)multiple.get(0);
        multiple =
          multiple.as(ref.as(reference.getReference().getName())
                      .withParameters(ref.getParameters()
                                      .fromProto(reference.getReference()
                                                 .getParameters())),
                      reference.hasPerDay()
                      ? ((Number)multiple.get(1)).as(reference.getPerDay())
                      : multiple.get(1));

        references.add(multiple);
      }

      m_specialAttacks = m_specialAttacks.as(references);
    }

    if(proto.getSpecialQualityCount() > 0)
    {
      List<Multiple> references = new ArrayList<>();
      for(BaseMonsterProto.QualityReference reference
        : proto.getSpecialQualityList())
      {
        Multiple multiple = m_specialQualities.createElement();

        Reference<BaseQuality> ref = (Reference<BaseQuality>)multiple.get(0);
        multiple =
          multiple.as(ref.as(reference.getReference().getName())
                      .withParameters(ref.getParameters()
                                      .fromProto(reference.getReference()
                                                 .getParameters())),
                      reference.hasCondition()
                      ? new Condition(reference.getCondition())
                      : multiple.get(1),
                      reference.hasPerDay()
                      ? ((Number)multiple.get(2)).as(reference.getPerDay())
                      : multiple.get(2));

        references.add(multiple);
      }

      m_specialQualities = m_specialQualities.as(references);
    }

    if(proto.getClassSkillCount() > 0)
    {
      List<Multiple> references = new ArrayList<>();
      for(BaseMonsterProto.SkillReference reference
        : proto.getClassSkillList())
      {
        Multiple multiple = m_classSkills.createElement();

        Reference<BaseSkill> ref = (Reference<BaseSkill>)multiple.get(0);
        multiple =
          multiple.as(ref.as(reference.getReference().getName())
                      .withParameters(ref.getParameters()
                                      .fromProto(reference.getReference()
                                                 .getParameters())),
                      reference.hasModifier()
                      ? ((Modifier)multiple.get(1))
                        .fromProto(reference.getModifier())
                      : multiple.get(1));

        references.add(multiple);
      }

      m_classSkills = m_classSkills.as(references);
    }

    if(proto.getFeatCount() > 0)
    {
      List<Reference<BaseFeat>> references = new ArrayList<>();
      for(BaseMonsterProto.Reference feat : proto.getFeatList())
      {
        Reference<BaseFeat> ref = m_feats.createElement();
        ref = ref.as(feat.getName())
          .withParameters(ref.getParameters().fromProto(feat.getParameters()));
        references.add(ref);
      }

      m_feats = m_feats.as(references);
    }

    if(proto.hasClimate() && proto.hasTerrain())
      m_environment = m_environment
        .as(((EnumSelection<Climate>)m_environment.get(0))
            .as(Climate.fromProto(proto.getClimate())),
            ((EnumSelection<Terrain>)m_environment.get(1))
            .as(Terrain.fromProto(proto.getTerrain())));
    else if(proto.hasClimate())
      m_environment = m_environment
        .as(((EnumSelection<Climate>)m_environment.get(0))
            .as(Climate.fromProto(proto.getClimate())),
            m_environment.get(1));
    else if(proto.hasTerrain())
      m_environment = m_environment
        .as(m_environment.get(0),
            ((EnumSelection<Terrain>)m_environment.get(1))
            .as(Terrain.fromProto(proto.getTerrain())));

    if(proto.getOrganizationCount() > 0)
    {
      List<Multiple> organizations = new ArrayList<>();
      for(BaseMonsterProto.Organization org : proto.getOrganizationList())
      {
        Multiple multiple = m_organizations.createElement();
        List<Multiple> pluses = new ArrayList<>();
        for(BaseMonsterProto.Organization.Plus plus : org.getPlusList())
        {
          Multiple plusMultiple =
            ((ValueList<Multiple>)multiple.get(2)).createElement();
          pluses.add(plusMultiple.as(((Dice)plusMultiple.get(0))
                                     .fromProto(plus.getNumber()),
                                     ((Name)plusMultiple.get(1))
                                     .as(plus.getText())));
        }

        multiple = multiple.as(org.hasType()
                               ? ((EnumSelection<Organization>)multiple.get(0))
                                 .as(Organization.fromProto(org.getType()))
                               : multiple.get(0),
                               org.hasNumber()
                               ? ((Dice)multiple.get(1))
                                 .fromProto(org.getNumber())
                               : multiple.get(1),
                               pluses.isEmpty()
                               ? multiple.get(2)
                               : ((ValueList<Multiple>)multiple.get(2))
                                 .as(pluses));
        organizations.add(multiple);
      }

      m_organizations = m_organizations.as(organizations);
    }

    if(proto.hasChallengeRating())
      m_cr = Rational.fromProto(proto.getChallengeRating());

    if(proto.hasTreasure())
      m_treasure = m_treasure.as(Treasure.fromProto(proto.getTreasure()));

    if(proto.hasAlignment() && proto.hasAlignmentStatus())
      m_alignment = m_alignment.as
      (((EnumSelection<AlignmentStatus>)m_alignment.get(0))
       .as(AlignmentStatus.fromProto(proto.getAlignmentStatus())),
       ((EnumSelection<Alignment>)m_alignment.get(1))
       .as(Alignment.fromProto(proto.getAlignment())));
    else if(proto.hasAlignmentStatus())
      m_alignment = m_alignment.as
      (((EnumSelection<AlignmentStatus>)m_alignment.get(0))
       .as(AlignmentStatus.fromProto(proto.getAlignmentStatus())),
       m_alignment.get(1));
    else if(proto.hasAlignment())
      m_alignment = m_alignment.as
       (m_alignment.get(0),
       ((EnumSelection<Alignment>)m_alignment.get(1))
       .as(Alignment.fromProto(proto.getAlignment())));

    if(proto.getAdvancementCount() > 0)
    {
      List<Multiple> advancements = new ArrayList<>();
      for(BaseMonsterProto.Advancement advancement : proto.getAdvancementList())
      {
        Multiple multiple = m_advancements.createElement();
        multiple =
          multiple.as(((Range)multiple.get(0))
                      .fromProto(advancement.getRange()),
                      ((EnumSelection<Size>)multiple.get(1))
                      .as(Size.fromProto(advancement.getSize())));
        advancements.add(multiple);
      }

      m_advancements = m_advancements.as(advancements);
    }

    if(proto.hasLevelAdjustment())
      if(proto.getLevelAdjustment() == 0)
        m_levelAdjustment =
          m_levelAdjustment.as(0, ((Selection)m_levelAdjustment.get(0))
                               .as(0));
      else
        m_levelAdjustment =
          m_levelAdjustment.as(1, ((Number)m_levelAdjustment.get(1))
                               .as(proto.getLevelAdjustment()));

    if(proto.getLanguageCount() > 0)
    {
      List<Multiple> languages = new ArrayList<>();
      for(BaseMonsterProto.Language language : proto.getLanguageList())
      {
        Multiple multiple = m_languages.createElement();
        if(language.hasModifier())
          multiple =
            multiple.as(((EnumSelection<LanguageModifier>)multiple.get(0))
                        .as(LanguageModifier.fromProto(language.getModifier())),
                        ((EnumSelection<Language>)multiple.get(1))
                        .as(Language.fromProto(language.getName())));
        else
          multiple =
            multiple.as(multiple.get(0),
                        ((EnumSelection<Language>)multiple.get(1))
                        .as(Language.fromProto(language.getName())));

        languages.add(multiple);
      }

      m_languages = m_languages.as(languages);
    }

    if(proto.hasEncounter())
      m_encounter = m_encounter.as(proto.getEncounter());

    if(proto.hasCombat())
      m_combat = m_combat.as(proto.getCombat());

    if(proto.hasTactics())
      m_tactics = m_tactics.as(proto.getTactics());

    if(proto.hasCharacter())
      m_character = m_character.as(proto.getCharacter());

    if(proto.hasReproduction())
      m_reproduction = m_reproduction.as(proto.getReproduction());

    if(proto.getPossessionCount() > 0)
    {
      List<Multiple> possessions = new ArrayList<>();
      for(BaseMonsterProto.Possession possession : proto.getPossessionList())
      {
        Multiple multiple = m_possessions.createElement();
        if(possession.hasName() && possession.hasText())
          multiple =
            multiple.as(((Name)multiple.get(0)).as(possession.getName()),
                       ((Text)multiple.get(1)).as(possession.getText()));
        else if(possession.hasName())
          multiple =
            multiple.as(((Name)multiple.get(0)).as(possession.getName()),
                        multiple.get(1));
        else if(possession.hasText())
          multiple =
            multiple.as(multiple.get(0),
                       ((Text)multiple.get(1)).as(possession.getText()));

        possessions.add(multiple);
      }

      m_possessions = m_possessions.as(possessions);
    }

    if(proto.getGoodSaveCount() > 0)
    {
      List<EnumSelection<Save>> saves = new ArrayList<>();
      for(BaseMonsterProto.Save save : proto.getGoodSaveList())
        saves.add(m_goodSaves.createElement().as(Save.fromProto(save)));

      m_goodSaves = m_goodSaves.as(saves);
    }

    super.fromProto(proto.getBase());
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(BaseMonsterProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- createBaseMonster() --------------------------------------------

    /**
     * Create a typical base item for testing purposes.
     *
     * @return the newly created base item
     *
     */
    public static AbstractEntry createBaseMonster()
    {
      try (ParseReader reader =
        new ParseReader(new java.io.StringReader(s_text), "test"))
      {
        return null; //BaseMonster.read(reader);
      }
    }

    //......................................................................

    //----- text -----------------------------------------------------------

    /** Test text. */
    private static String s_text =
      "#----- Aboleth -------------------------------------------------\n"
      + "\n"
      + "base monster Aboleth = \n"
      + "\n"
      + "  size              huge (long);\n"
      + "  type              Aberration (Aquatic);\n"
      + "  hit dice          8d8;\n"
      + "  speed             10 ft, swim 60 ft;\n"
      + "  natural armor     +7;\n"
      + "  base attack       +6;  \n"
      + "  primary attacks   4 tentacle melee (1d6 plus slime);  \n"
      + "  special attacks   enslave, psionics, slime;\n"
      + "  special qualities darkvision [range 60 ft], mucus cloud, breathe "
      + "water, \n"
      + "                    breathe no air, good swimmer [racial +8];\n"
      + "  strength          26;\n"
      + "  dexterity         12;\n"
      + "  constitution      20;\n"
      + "  intelligence      15;\n"
      + "  wisdom            17;\n"
      + "  charisma          17;\n"
      + "  class skills      Concentration: +11, Knowledge [subtype any one]: "
      + "+11, \n"
      + "                    Listen: +11, Spot: +11, Swim: 0;\n"
      + "  feats             Alertness, Combat Casting, Iron Will;\n"
      + "  environment       underground;\n"
      + "  organization      solitary, brood 1d3+1, \n"
      + "                    slaver brood 1d3+1 plus 1d6+6 skum;\n"
      + "  challenge rating  7;\n"
      + "  treasure          double standard;\n"
      + "  alignment         usually lawful evil;\n"
      + "  advancements      9-16 HD (Huge), 17-24 HD (Gargantuan);\n"
      + "  level adjustment  -;\n"
      + "  worlds            generic;\n"
      + "  references        WTC 17755: 8-9;\n"
      + "  short description \"short description\";\n"
      + "  encounter         \"encounter\";\n"
      + "  combat            \"combat\";\n"
      + "  languages         Aboleth, Undercommon, Aquan;\n"
      + "  tactics           \"tactic\";\n"
      + "  character         \"character\";\n"
      + "  reproduction      \"reproduction\";\n"
      + "  description \n"
      + "\n"
      + "  \"description\".\n"
      + "\n"
      + "#...............................................................\n";

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Test reading. */
    @org.junit.Test
    public void testRead()
    {
      String result =
      "#----- Aboleth\n"
      + "\n"
      + "base monster Aboleth =\n"
      + "\n"
      + "  size              Huge (long);\n"
      + "  type              Aberration (Aquatic);\n"
      + "  hit dice          8d8;\n"
      + "  speed             10 ft, Swim 60 ft;\n"
      + "  natural armor     +7 natural armor;\n"
      + "  base attack       +6;\n"
      + "  strength          26;\n"
      + "  dexterity         12;\n"
      + "  constitution      20;\n"
      + "  intelligence      15;\n"
      + "  wisdom            17;\n"
      + "  charisma          17;\n"
      + "  primary attacks   4 Tentacle melee (1d6 plus slime);\n"
      + "  special attacks   enslave, psionics, slime;\n"
      + "  special qualities darkvision [range 60 ft], mucus cloud, breathe "
      + "water, breathe no air, good swimmer [racial +8];\n"
      + "  class skills      Concentration: +11 general, "
      + "Knowledge [subtype Any One]: +11 general, Listen: +11 general, "
      + "Spot: +11 general, Swim: +0 general;\n"
      + "  feats             Alertness, Combat Casting, Iron Will;\n"
      + "  environment       Underground;\n"
      + "  organization      Solitary, Brood 1d3 +1, Slaver Brood 1d3 +1 "
      + "plus 1d6 +6 skum;\n"
      + "  challenge rating  7;\n"
      + "  treasure          double standard;\n"
      + "  alignment         Usually Lawful Evil;\n"
      + "  advancements      9-16 HD (Huge), 17-24 HD (Gargantuan);\n"
      + "  level adjustment  -;\n"
      + "  languages         Aboleth, Undercommon, Aquan;\n"
      + "  encounter         \"encounter\";\n"
      + "  combat            \"combat\";\n"
      + "  tactics           \"tactic\";\n"
      + "  character         \"character\";\n"
      + "  reproduction      \"reproduction\";\n"
      + "  possessions       ;\n"
      + "  worlds            Generic;\n"
      + "  references        WTC 17755: 8-9;\n"
      + "  description       \"description\";\n"
      + "  short description \"short description\";\n"
      + "  name              Aboleth.\n"
      + "\n"
      + "#.....\n";

      AbstractEntry entry = createBaseMonster();

      assertNotNull("base item should have been read", entry);
      assertEquals("base item name does not match", "Aboleth",
                   entry.getName());
      assertEquals("base item does not match", result, entry.toString());
    }

    //......................................................................
  }

  //........................................................................
}
