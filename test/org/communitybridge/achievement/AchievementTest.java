package org.communitybridge.achievement;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.communitybridge.main.BukkitWrapper;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.Environment;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AchievementTest
{
	Player player = mock(Player.class);
	Environment environment = new Environment();
	Configuration configuration = mock(Configuration.class);
	PlayerInventory playerInventory = mock(PlayerInventory.class);
	PlayerInventory otherInventory = mock(PlayerInventory.class);
	BukkitWrapper bukkit = mock(BukkitWrapper.class);
	Server server = mock(Server.class);

	Achievement achievement = new TestableAchievement(environment, bukkit);

	@Before
	public void beforeEach()
	{
		environment.setConfiguration(configuration);
	}

	@Test
	public void rewardPlayerPerformsCashReward()
	{
		Double amount = RandomUtils.nextDouble(10.0, 1000.0);
		configuration.economyEnabled = true;
		Economy economy = mock(Economy.class);
		environment.setEconomy(economy);
		when(economy.depositPlayer(player, amount)).thenReturn(null);
		achievement.setCashReward(amount);
		achievement.rewardPlayer(player, null);
		verify(economy).depositPlayer(player, amount);
	}

	@Test
	public void rewardPlayerNoEconomyNoCashAward()
	{
		Double amount = RandomUtils.nextDouble(10.0, 1000.0);
		configuration.economyEnabled = false;
		Economy economy = mock(Economy.class);
		environment.setEconomy(economy);
		when(economy.depositPlayer(player, amount)).thenReturn(null);
		achievement.setCashReward(amount);
		achievement.rewardPlayer(player, null);
		verifyZeroInteractions(economy);
	}

	@Test
	public void rewardPlayerOneItem()
	{
		configuration.economyEnabled = false;
		Map<Material, Integer> itemRewards = new EnumMap<Material, Integer>(Material.class);
		itemRewards.put(Material.ACACIA_STAIRS, 10);
		achievement.setItemRewards(itemRewards);
		when(player.getInventory()).thenReturn(playerInventory);
		when(playerInventory.addItem(any(ItemStack.class))).thenReturn(null);
		achievement.rewardPlayer(player, null);
		verify(playerInventory).addItem(any(ItemStack.class));
		verify(player).updateInventory();
	}

	@Test
	public void rewardPlayerTwoItems()
	{
		configuration.economyEnabled = false;
		setupRewards();
		when(player.getInventory()).thenReturn(playerInventory);
		when(playerInventory.addItem(any(ItemStack.class))).thenReturn(null);
		achievement.rewardPlayer(player, null);
		verify(playerInventory, times(2)).addItem(any(ItemStack.class));
		verify(player, times(1)).updateInventory();
	}

	@Test
	public void rewardPlayerNoItemDontUpdateInventory()
	{
		configuration.economyEnabled = false;
		achievement.rewardPlayer(player, null);
		verify(player, never()).updateInventory();
	}

	@Test
	public void canRewardAllItemRewardsReturnTrue()
	{
		setupRewards();

		when(bukkit.getServer()).thenReturn(server);
		when(server.createInventory(null, InventoryType.PLAYER)).thenReturn(otherInventory);
		when(player.getInventory()).thenReturn(playerInventory);
		when(playerInventory.getType()).thenReturn(InventoryType.PLAYER);
		assertTrue(achievement.canRewardAllItemRewards(player));
	}

	@Test
	public void canRewardAllItemRewardsReturnFalseWhenTooMany()
	{
		setupRewards();

		HashMap<Integer, ItemStack> rejected = new HashMap<Integer, ItemStack>();
		ItemStack stack = new ItemStack(Material.ACACIA_STAIRS, 64);
		rejected.put(0, stack);

		when(bukkit.getServer()).thenReturn(server);
		when(server.createInventory(null, InventoryType.PLAYER)).thenReturn(otherInventory);
		when(player.getInventory()).thenReturn(playerInventory);
		when(playerInventory.getType()).thenReturn(InventoryType.PLAYER);
		when(otherInventory.addItem(any(ItemStack.class))).thenReturn(rejected);

		assertFalse(achievement.canRewardAllItemRewards(player));
	}

	private void setupRewards()
	{
		Map<Material, Integer> itemRewards = new EnumMap<Material, Integer>(Material.class);
		itemRewards.put(Material.ACACIA_STAIRS, 10);
		itemRewards.put(Material.ACTIVATOR_RAIL, 10);
		achievement.setItemRewards(itemRewards);
	}

	public class TestableAchievement extends Achievement
	{
		public TestableAchievement(Environment environment, BukkitWrapper bukkit)
		{
			super(environment);
			this.bukkit = bukkit;
		}

		@Override
		public boolean playerQualifies(Player player, PlayerAchievementState state)
		{
			return false;
		}
	}
}