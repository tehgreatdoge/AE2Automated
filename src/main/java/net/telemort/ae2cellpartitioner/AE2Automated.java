package net.telemort.ae2cellpartitioner;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(AE2Automated.MODID)
public class AE2Automated
{
    public static final String MODID = "ae2automated";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<Block> PARTITIONER_BLOCK = BLOCKS.register("cell_partitioner", () -> new CellPartitionerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)));
    public static final RegistryObject<Item> PARTITIONER_ITEM = ITEMS.register("cell_partitioner", () -> new BlockItem(PARTITIONER_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<CellPartitionerBlockEntity>> PARTITIONER_BLOCK_ENTITY_TYPE = BLOCK_ENTITY_TYPES.register("cell_partitioner", ()-> BlockEntityType.Builder.of(CellPartitionerBlockEntity::new, PARTITIONER_BLOCK.get()).build(null) );

    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup."+MODID+".main"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> PARTITIONER_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(PARTITIONER_ITEM.get());
            }).build());

    public AE2Automated()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }
}
