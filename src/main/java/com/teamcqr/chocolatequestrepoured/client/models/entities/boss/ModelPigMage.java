package com.teamcqr.chocolatequestrepoured.client.models.entities.boss;

import com.teamcqr.chocolatequestrepoured.client.models.entities.ModelCQRBiped;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Necromancer - Arlo The Epic
 * Created using Tabula 7.0.0
 */
public class ModelPigMage extends ModelCQRBiped {
    public ModelRenderer Leg_Cape;
    public ModelRenderer Buckle;
    public ModelRenderer Hood_Rear;
    public ModelRenderer Eyes;
    public ModelRenderer Hood_Top;
    public ModelRenderer Hood_Bottom;
    public ModelRenderer Hood_Left;
    public ModelRenderer Hood_Right;
    public ModelRenderer Hood_Front;
    public ModelRenderer Arm_Left_Bone;
    public ModelRenderer Arm_Right_Bone;

    public ModelPigMage(float modelSize) {
    	this(modelSize, 0, 128, 64);
    }
    
    public ModelPigMage(float modelSize, float p_i1149_2_, int textureW, int textureH) {
        //this.textureWidth = 128;
        //this.textureHeight = 64;
    	super(modelSize, p_i1149_2_, textureW, textureH, false);
        this.bipedLeftArm = new ModelRenderer(this, 32, 34);
        this.bipedLeftArm.setRotationPoint(4.0F, 2.0F, 0.0F);
        this.bipedLeftArm.addBox(-0.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F);
        this.Hood_Front = new ModelRenderer(this, 60, 23);
        this.Hood_Front.setRotationPoint(0.0F, 0.0F, -9.0F);
        this.Hood_Front.addBox(-4.5F, 0.0F, 0.0F, 9, 11, 0, 0.0F);
        this.Hood_Rear = new ModelRenderer(this, 42, 23);
        this.Hood_Rear.setRotationPoint(0.0F, 0.0F, 4.5F);
        this.Hood_Rear.addBox(-4.5F, -10.5F, 0.0F, 9, 11, 0, 0.0F);
        this.Hood_Left = new ModelRenderer(this, 24, 14);
        this.Hood_Left.setRotationPoint(4.5F, 0.0F, 0.0F);
        this.Hood_Left.addBox(0.0F, 0.0F, -9.0F, 0, 11, 9, 0.0F);
        this.Hood_Top = new ModelRenderer(this, 24, 14);
        this.Hood_Top.setRotationPoint(0.0F, -10.5F, 0.0F);
        this.Hood_Top.addBox(-4.5F, 0.0F, -9.0F, 9, 0, 9, 0.0F);
        this.Arm_Right_Bone = new ModelRenderer(this, 51, 2);
        this.Arm_Right_Bone.setRotationPoint(-2.0F, 6.0F, 0.0F);
        this.Arm_Right_Bone.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F);
        this.bipedRightArm = new ModelRenderer(this, 32, 34);
        this.bipedRightArm.setRotationPoint(-4.0F, 2.0F, 0.0F);
        this.bipedRightArm.addBox(-4.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F);
        this.bipedLeftLeg = new ModelRenderer(this, 0, 34);
        this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.Buckle = new ModelRenderer(this, 78, 2);
        this.Buckle.setRotationPoint(0.0F, 12.0F, -2.1F);
        this.Buckle.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 0, 0.0F);
        this.Leg_Cape = new ModelRenderer(this, 3, 21);
        this.Leg_Cape.setRotationPoint(0.0F, 12.0F, 2.0F);
        this.Leg_Cape.addBox(-4.0F, 0.0F, 0.0F, 8, 12, 0, 0.0F);
        this.setRotateAngle(Leg_Cape, 0.2792526803190927F, 0.0F, 0.0F);
        this.Arm_Left_Bone = new ModelRenderer(this, 51, 2);
        this.Arm_Left_Bone.setRotationPoint(2.0F, 6.0F, 0.0F);
        this.Arm_Left_Bone.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F);
        this.bipedBody = new ModelRenderer(this, 0, 18);
        this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
        this.bipedHead = new ModelRenderer(this, 83, 0);
        this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.setRotateAngle(bipedHead, 0.0F, 0.017453292519943295F, 0.0F);
        this.Hood_Right = new ModelRenderer(this, 24, 14);
        this.Hood_Right.setRotationPoint(-4.5F, 0.0F, 0.0F);
        this.Hood_Right.addBox(0.0F, 0.0F, -9.0F, 0, 11, 9, 0.0F);
        this.Eyes = new ModelRenderer(this, 54, 18);
        this.Eyes.setRotationPoint(0.0F, -4.0F, -4.1F);
        this.Eyes.addBox(-3.0F, 0.0F, 0.0F, 6, 1, 0, 0.0F);
        this.Hood_Bottom = new ModelRenderer(this, 0, 0);
        this.Hood_Bottom.setRotationPoint(0.0F, 0.5F, 0.0F);
        this.Hood_Bottom.addBox(-4.5F, 0.0F, -9.0F, 9, 0, 9, 0.0F);
        this.bipedRightLeg = new ModelRenderer(this, 0, 34);
        this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        
        this.bipedBody.addChild(this.bipedLeftArm);
        this.Hood_Top.addChild(this.Hood_Front);
        this.bipedHead.addChild(this.Hood_Rear);
        this.Hood_Top.addChild(this.Hood_Left);
        this.Hood_Rear.addChild(this.Hood_Top);
        this.bipedRightArm.addChild(this.Arm_Right_Bone);
        this.bipedBody.addChild(this.bipedRightArm);
        this.bipedBody.addChild(this.bipedLeftLeg);
        this.bipedBody.addChild(this.Buckle);
        this.bipedBody.addChild(this.Leg_Cape);
        this.bipedLeftArm.addChild(this.Arm_Left_Bone);
        this.bipedBody.addChild(this.bipedHead);
        this.Hood_Top.addChild(this.Hood_Right);
        this.bipedHead.addChild(this.Eyes);
        this.Hood_Rear.addChild(this.Hood_Bottom);
        this.bipedBody.addChild(this.bipedRightLeg);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.bipedBody.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
