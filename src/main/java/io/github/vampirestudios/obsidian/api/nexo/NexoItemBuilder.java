/*
package io.github.vampirestudios.obsidian.api.nexo;

import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public class NexoItemBuilder {
  private final NexoItem item = new NexoItem();

  public static NexoItemBuilder create(String key) {
    item.id = new Identifier(MY_MOD_ID, key);
    return new NexoItemBuilder();
  }

  public NexoItemBuilder material(String mat) {
    item.material = mat;
    return this;
  }

  public NexoItemBuilder pack(Consumer<PackBuilder> c) {
    var pb = new PackBuilder();
    c.accept(pb);
    item.pack = pb.build(item.id);
    return this;
  }

  public NexoItemBuilder mechanics(Consumer<MechanicsBuilder> c) {
    var mb = new MechanicsBuilder();
    c.accept(mb);
    item.mechanics = mb.build();
    return this;
  }

  public NexoItemBuilder itemName(String name) {
    item.itemName = name;
    return this;
  }

  public NexoItem build() {
    return item;
  }

  //––– nested builders –––//

  public static class PackBuilder {
    private final NexoItem.Pack p = new NexoItem.Pack();

    public PackBuilder model(String loc) {
      p.model = Identifier.parse(loc);
      return this;
    }

    public PackBuilder customModelData(int d) {
      p.custom_model_data = d;
      return this;
    }

    public NexoItem.Pack build(Identifier id) {
      p.id = id;
      return p;
    }
  }

  public static class MechanicsBuilder {
    private final NexoItem.Mechanics m = new NexoItem.Mechanics();

    public FurnitureBuilder furniture() {
      return new FurnitureBuilder(this, m);
    }

    public NexoItem.Mechanics build() {
      return m;
    }
  }

  public static class FurnitureBuilder {
    private final NexoItem.Mechanics m;
    private final NexoItem.Mechanics.Furniture f = new NexoItem.Mechanics.Furniture();

    public FurnitureBuilder(MechanicsBuilder parent, NexoItem.Mechanics mech) {
      this.m = mech;
      mech.furniture = f;
    }

    public FurnitureBuilder rotatable(boolean b) {
      f.rotatable = b;
      return this;
    }

    public FurnitureBuilder restrictedRotation(NexoItem.Mechanics.Furniture.RestrictedRotation r) {
      f.restricted_rotation = r;
      return this;
    }

    public FurnitureBuilder limitedPlacing(boolean roof, boolean floor, boolean wall) {
      var lp = new NexoItem.Mechanics.LimitedPlacing();
      lp.roof = roof; lp.floor = floor; lp.wall = wall;
      f.limited_placing = lp;
      return this;
    }

    public FurnitureBuilder blockSounds(String place, String brk) {
      var bs = new NexoItem.Mechanics.BlockSounds();
      bs.place_sound = Identifier.of(place);
      bs.break_sound = Identifier.of(brk);
      f.block_sounds = bs;
      return this;
    }

    public FurnitureBuilder hitbox(Consumer<HitboxBuilder> c) {
      var hb = new HitboxBuilder(this, f);
      c.accept(hb);
      return this;
    }

    public FurnitureBuilder seats(double... coords) {
      // coords in x,y,z triples
      for (int i = 0; i < coords.length; i += 3) {
        f.seats.add(String.format("%s,%s,%s", coords[i], coords[i+1], coords[i+2]));
      }
      return this;
    }

    public FurnitureBuilder storage(Consumer<StorageBuilder> c) {
      var sb = new StorageBuilder(this, f);
      c.accept(sb);
      return this;
    }

    public FurnitureBuilder lights(Consumer<LightsBuilder> c) {
      var lb = new LightsBuilder(this, f);
      c.accept(lb);
      return this;
    }

    public MechanicsBuilder done() {
      return */
/* the parent MechanicsBuilder *//*
;
    }
  }

  // … similarly: HitboxBuilder, StorageBuilder, LightsBuilder …
}
*/
