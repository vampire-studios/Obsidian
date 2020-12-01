package io.github.vampirestudios.obsidian;

import com.google.gson.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.*;

import java.lang.reflect.Type;
import java.util.stream.Stream;

public final class GeometryCuboid {

    private Face[] faces;

    private Vec3f rotation, pivot;

    private final Vec3d size, origin;
    private final float inflate, u, v;
    private final boolean mirror;

    public GeometryCuboid(Vec3d origin, Vec3d size, float inflate, float u, float v, boolean mirror) {
        this.origin = origin; this.size = size;
        this.inflate = inflate;
        this.u = u; this.v = v;
        this.mirror = mirror;
    }

    public void defineFaces(int texWidth, int texHeight) {
        Vec3d secondCorner = origin.add(size);

        this.faces = new Face[6];
        Vec3d inflatedMin = origin.subtract(inflate, inflate, inflate);
        Vec3d inflatedMax = secondCorner.add(inflate, inflate, inflate);

        if (mirror) {
            double x = inflatedMax.getX();
            inflatedMax = new Vec3d(inflatedMin.getX(), inflatedMax.getY(), inflatedMax.getZ());
            inflatedMin = new Vec3d(x, inflatedMin.getY(), inflatedMin.getZ());
        }

        TexturedVertex[] vertices = new TexturedVertex[8];
        vertices[0] = new TexturedVertex(new Vec3d(inflatedMin.x, inflatedMin.y, inflatedMin.z), 0.0F, 0.0F);
        vertices[1] = new TexturedVertex(new Vec3d(inflatedMax.x, inflatedMin.y, inflatedMin.z), 0.0F, 8.0F);
        vertices[2] = new TexturedVertex(new Vec3d(inflatedMax.x, inflatedMax.y, inflatedMin.z), 8.0F, 8.0F);
        vertices[3] = new TexturedVertex(new Vec3d(inflatedMin.x, inflatedMax.y, inflatedMin.z), 8.0F, 0.0F);
        vertices[4] = new TexturedVertex(new Vec3d(inflatedMin.x, inflatedMin.y, inflatedMax.z), 0.0F, 0.0F);
        vertices[5] = new TexturedVertex(new Vec3d(inflatedMax.x, inflatedMin.y, inflatedMax.z), 0.0F, 8.0F);
        vertices[6] = new TexturedVertex(new Vec3d(inflatedMax.x, inflatedMax.y, inflatedMax.z), 8.0F, 8.0F);
        vertices[7] = new TexturedVertex(new Vec3d(inflatedMin.x, inflatedMax.y, inflatedMax.z), 8.0F, 0.0F);

        double k = u + size.getZ();
        double l = u + size.getZ() + size.getX();
        double m = u + size.getZ() + size.getX() + size.getX();
        double n = u + size.getZ() + size.getX() + size.getZ();
        double o = u + size.getZ() + size.getX() + size.getZ() + size.getX();
        double q = v + size.getZ();
        double r = v + size.getZ() + size.getY();

        this.faces[2] = new Face(new TexturedVertex[]{vertices[1], vertices[0], vertices[4], vertices[5]}, l, q, m, v, texWidth, texHeight, mirror, Direction.DOWN);
        this.faces[3] = new Face(new TexturedVertex[]{vertices[6], vertices[7], vertices[3], vertices[2]}, k, v, l, q, texWidth, texHeight, mirror, Direction.UP);
        this.faces[1] = new Face(new TexturedVertex[]{vertices[3], vertices[7], vertices[4], vertices[0]}, u, q, k, r, texWidth, texHeight, mirror, Direction.WEST);
        this.faces[4] = new Face(new TexturedVertex[]{vertices[2], vertices[3], vertices[0], vertices[1]}, k, q, l, r, texWidth, texHeight, mirror, Direction.NORTH);
        this.faces[0] = new Face(new TexturedVertex[]{vertices[6], vertices[2], vertices[1], vertices[5]}, l, q, n, r, texWidth, texHeight, mirror, Direction.EAST);
        this.faces[5] = new Face(new TexturedVertex[]{vertices[7], vertices[6], vertices[5], vertices[4]}, n, q, o, r, texWidth, texHeight, mirror, Direction.SOUTH);

    }

    public void setCuboidRotation(float x, float y, float z, float pitch, float yaw, float roll) {
        this.pivot = new Vec3f(x, y, z);
        this.rotation = new Vec3f(pitch, yaw, roll);
    }

    public void renderCuboid(MatrixStack stack, VertexConsumer consumer, int light, int overlay, float red, float green, float blue, float alpha) {
        stack.push();

        if(rotation != null) {
            stack.translate(pivot.getX() / 16F, pivot.getY() / 16F, pivot.getZ() / 16F);
            stack.multiply(new Quaternion(-rotation.getX(), rotation.getY(), -rotation.getZ(), false));
            stack.translate(-(pivot.getX() / 16F), -(pivot.getY() / 16F), -(pivot.getZ() / 16F));
        }

        Matrix4f modelMatrix = stack.peek().getModel();
        Matrix3f normalMatrix = stack.peek().getNormal();

        Stream.of(faces).forEach(f -> {
            Vec3f normal = f.normal.copy();
            normal.transform(normalMatrix);

            Stream.of(f.vertices).forEach(v -> {
                float x = (float)v.pos.getX() / 16.0F;
                float y = (float)v.pos.getY() / 16.0F;
                float z = (float)v.pos.getZ() / 16.0F;
                Vector4f pos = new Vector4f(x, y, z, 1.0F);
                pos.transform(modelMatrix);
                consumer.vertex(
                        pos.getX(), pos.getY(), pos.getZ(),
                        red, green, blue, alpha,
                        (float)v.u, (float)v.v, overlay, light,
                        normal.getX(), normal.getY(), normal.getZ());
            });
        });

        stack.pop();
    }

    private static final class Face {
        private final TexturedVertex[] vertices;
        private final Vec3f normal;

        public Face(TexturedVertex[] vertices, double u1, double v1, double u2, double v2, float squishU, float squishV, boolean mirror, Direction d) {
            this.vertices = vertices;
            float uMod = 0.0F / squishU;
            float vMod = 0.0F / squishV;

            vertices[0] = vertices[0].uvRemap(u2 / squishU - uMod, v1 / squishV + vMod);
            vertices[1] = vertices[1].uvRemap(u1 / squishU + uMod, v1 / squishV + vMod);
            vertices[2] = vertices[2].uvRemap(u1 / squishU + uMod, v2 / squishV - vMod);
            vertices[3] = vertices[3].uvRemap(u2 / squishU - uMod, v2 / squishV - vMod);

            this.normal = d.getUnitVector();
            this.normal.multiplyComponentwise(1F, -1F, -1F);

            if(mirror) {
                for(int i = 0; i < vertices.length / 2; ++i) {
                    TexturedVertex vertex = vertices[i];
                    vertices[i] = vertices[vertices.length - 1 - i];
                    vertices[vertices.length - 1 - i] = vertex;
                }
                this.normal.multiplyComponentwise(-1.0F, 1.0F, 1.0F);
            }
        }
    }

    private static final class TexturedVertex {
        public final Vec3d pos;
        public final double u, v;

        public TexturedVertex(Vec3d pos, double u, double v) {
            this.pos = pos;
            this.u = u; this.v = v;
        }

        public TexturedVertex uvRemap(double u, double v) {
            return new TexturedVertex(this.pos, u, v);
        }
    }

    public static class Deserializer implements JsonDeserializer<GeometryCuboid> {

        @Override
        public GeometryCuboid deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            JsonArray originJson = obj.getAsJsonArray("origin");
            Vec3d origin = new Vec3d(originJson.get(0).getAsDouble(), originJson.get(1).getAsDouble(), originJson.get(2).getAsDouble());
            JsonArray sizeJson = obj.getAsJsonArray("size");
            Vec3d size = new Vec3d(sizeJson.get(0).getAsDouble(), sizeJson.get(1).getAsDouble(), sizeJson.get(2).getAsDouble());
            JsonArray uv = obj.getAsJsonArray("uv");
            float u = uv.get(0).getAsFloat(); float v = uv.get(1).getAsFloat();
            boolean mirror = obj.has("mirror") && obj.get("mirror").getAsBoolean();

            float inflate = 0;
            if(obj.has("inflate"))
                inflate = obj.get("inflate").getAsFloat();

            GeometryCuboid cube = new GeometryCuboid(origin, size, inflate, u, v, mirror);

            if(obj.has("rotation") && obj.has("pivot")) {
                JsonArray pivot = obj.getAsJsonArray("pivot");
                JsonArray rot = obj.getAsJsonArray("rotation");
                cube.setCuboidRotation(
                        pivot.get(0).getAsFloat(), pivot.get(1).getAsFloat(), pivot.get(2).getAsFloat(),
                        (float)Math.toRadians(rot.get(0).getAsFloat()), (float)Math.toRadians(rot.get(1).getAsFloat()), (float)Math.toRadians(rot.get(2).getAsFloat()));
            }

            return cube;
        }
    }
}