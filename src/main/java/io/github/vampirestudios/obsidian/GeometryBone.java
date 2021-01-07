package io.github.vampirestudios.obsidian;

import com.google.gson.*;
import io.github.vampirestudios.obsidian.client.ClientInit;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GeometryBone {

    public final String bone_id;

    private final List<GeometryBone> children = new ArrayList<>();
    private final List<GeometryCuboid> elements = new ArrayList<>();

    public boolean hasParent = false;
    public String parent;

    private final Vector3f pivot;
    private Vector3f rotation = new Vector3f(0, 0, 0);

    private final Vector3f additiveRotation = new Vector3f(0 ,0 ,0);
    private final Vector3f additiveTranslation = new Vector3f(0, 0,0 );
    private final Vector3f additiveScaling = new Vector3f(1, 1, 1);

    public boolean visible = true;

    public GeometryBone(String id, Vector3f pivot) {
        this.bone_id = id;
        this.pivot = pivot;
    }

    public void setParent(String parent) {
        this.parent = parent;
        this.hasParent = true;
    }

    public void setRotation(float pitch, float yaw, float roll, boolean degrees) {
        if(degrees)
            this.rotation = new Vector3f((float)Math.toRadians(pitch), (float)Math.toRadians(yaw), (float)Math.toRadians(roll));
        else
            this.rotation = new Vector3f(pitch, yaw, roll);
    }

    public void addRotation(float pitch, float yaw, float roll, boolean degrees) {
        if(degrees)
            this.additiveRotation.add(new Vector3f((float)Math.toRadians(pitch), (float)Math.toRadians(yaw), (float)Math.toRadians(roll)));
        else
            this.additiveRotation.add(new Vector3f(pitch, yaw, roll));
    }

    public void addTranslation(float x, float y, float z) {
        this.additiveTranslation.add(new Vector3f(x, y, z));
    }

    public void addScaling(float x, float y, float z) {
        this.additiveScaling.add(new Vector3f(x, y, z));
    }

    public void setTextureSize(int width, int height) {
        this.elements.forEach(e -> e.defineFaces(width, height));
    }

    public void addChild(GeometryBone bone) {
        children.add(bone);
    }

    public void addCube(GeometryCuboid cube) {
        elements.add(cube);
    }

    public void render(MatrixStack stack, VertexConsumer consumer, int light, int overlay) {
        this.render(stack, consumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void render(MatrixStack stack, VertexConsumer consumer, int light, int overlay, float red, float green, float blue, float alpha) {
        if (this.visible) {

            stack.push();
            if (!this.elements.isEmpty() || !this.children.isEmpty()) {
                stack.translate(pivot.getX() / 16D, pivot.getY() / 16D, pivot.getZ() / 16D);
                stack.multiply(Vector3f.NEGATIVE_Z.getRadialQuaternion(rotation.getZ() + additiveRotation.getZ()));
                stack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(rotation.getY() + additiveRotation.getY()));
                stack.multiply(Vector3f.NEGATIVE_X.getRadialQuaternion(rotation.getX() + additiveRotation.getX()));
                stack.translate(-(pivot.getX()) / 16D, -(pivot.getY() / 16D), -(pivot.getZ() / 16D));

                stack.translate(additiveTranslation.getX(), additiveTranslation.getY(), additiveTranslation.getZ());
                stack.scale(additiveScaling.getX(), additiveScaling.getY(), additiveScaling.getZ());

                additiveRotation.set(0, 0, 0);
                additiveTranslation.set(0, 0, 0);
                additiveScaling.set(1, 1, 1);

                elements.forEach(c -> c.renderCuboid(stack, consumer, light, overlay, red, green, blue, alpha));
            }

            if (!this.children.isEmpty())
                children.forEach(c -> c.render(stack, consumer, light, overlay, red, green, blue, alpha));

            stack.pop();
        }
    }

    public static class Deserializer implements JsonDeserializer<GeometryBone> {

        @Override
        public GeometryBone deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            String id = obj.get("name").getAsString();
            JsonArray pivotJson = obj.get("pivot").getAsJsonArray();
            Vector3f pivot = new Vector3f(pivotJson.get(0).getAsFloat(), pivotJson.get(1).getAsFloat(), pivotJson.get(2).getAsFloat());

            GeometryBone bone = new GeometryBone(id, pivot);

            if(obj.has("parent"))
                bone.setParent(obj.get("parent").getAsString());

            if(obj.has("cubes")) {
                JsonArray cubes = obj.getAsJsonArray("cubes");
                cubes.forEach(e -> {
                    GeometryCuboid boneTmp = ClientInit.GSON_CLIENT.fromJson(e, GeometryCuboid.class);
                    bone.addCube(boneTmp);
                });
            }

            if(obj.has("rotation")) {
                JsonArray rot = obj.get("rotation").getAsJsonArray();
                bone.setRotation(rot.get(0).getAsFloat(), rot.get(1).getAsFloat(), rot.get(2).getAsFloat(), true);
            }

            return bone;
        }
    }
}