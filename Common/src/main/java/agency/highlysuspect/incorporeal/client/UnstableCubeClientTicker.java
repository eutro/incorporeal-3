package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.block.UnstableCubeBlock;
import agency.highlysuspect.incorporeal.block.entity.UnstableCubeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import vazkii.botania.common.proxy.IProxy;

public class UnstableCubeClientTicker {
	public static void clientTick(Level level, BlockPos pos, BlockState state, UnstableCubeBlockEntity self) {
		if(self.speed == 0) self.speed = 8;
		self.angle += self.speed;
		self.angle %= 360f;
		if(self.speed > 1f) self.speed *= 0.96;
		
		self.bump *= 0.8f;
		
		if(level.getGameTime() >= self.nextLightningTick) {
			//add ligtning particle
			DyeColor color = state.getBlock() instanceof UnstableCubeBlock ub ? ub.color : DyeColor.WHITE;
			int colorPacked = color.getTextColor();
			int colorDarker =
				(((colorPacked & 0xFF0000) >> 16) / 2) << 16 |
				(((colorPacked & 0x00FF00) >> 8) / 2) << 8 |
				(colorPacked & 0x0000FF) / 2;
			
			Vec3 start = Vec3.atCenterOf(pos);
			Vec3 end = start.add(level.random.nextDouble() * 2 - 1, level.random.nextDouble() * 2 - 1, level.random.nextDouble() * 2 - 1);
			IProxy.INSTANCE.lightningFX(start, end, 0.5f, colorPacked, colorDarker);
			
			//set the time until the next one
			self.nextLightningTick = level.getGameTime() + self.speed > 1.1f ?
				(int) (60 - Math.min(60, self.speed)) + 3 :
				level.random.nextInt(60) + 50;
			
			//play a sound
			float volume = self.speed > 1.1f ? self.speed / 170f : 0.1f;
			float pitch = basePitches[color.getId()] + (self.speed / 600f);
			if(volume > 0.7f) volume = 0.7f;
			if(self.speed > 83) pitch += 0.1f;
			
			level.playLocalSound(
				pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
				SoundEvents.COW_AMBIENT, SoundSource.BLOCKS,
				volume, pitch, false);
		}
	}
	
	private static final float[] basePitches = new float[] {1f, 1.1f, 1.15f, 1.2f, 1.25f, 1.3f, 1.35f, 1.4f, 0.9f, 0.85f, 0.8f, 0.75f, 0.7f, 0.65f, 0.6f, 0.55f};
}
