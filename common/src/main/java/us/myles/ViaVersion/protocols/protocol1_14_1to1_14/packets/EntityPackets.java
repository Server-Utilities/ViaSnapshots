package us.myles.ViaVersion.protocols.protocol1_14_1to1_14.packets;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.entities.Entity1_14Types;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.version.Types1_14;
import us.myles.ViaVersion.packets.State;
import us.myles.ViaVersion.protocols.protocol1_14_1to1_14.metadata.MetadataRewriter1_14_1To1_14;
import us.myles.ViaVersion.protocols.protocol1_14_1to1_14.storage.EntityTracker1_14_1;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.storage.EntityTracker1_14;

public class EntityPackets {

    public static void register(final Protocol protocol) {
        MetadataRewriter1_14_1To1_14 metadataRewriter = protocol.get(MetadataRewriter1_14_1To1_14.class);

        // Spawn Mob
        protocol.registerOutgoing(State.PLAY, 0x03, 0x03, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Type.UUID); // 1 - Entity UUID
                map(Type.VAR_INT); // 2 - Entity Type
                map(Type.DOUBLE); // 3 - X
                map(Type.DOUBLE); // 4 - Y
                map(Type.DOUBLE); // 5 - Z
                map(Type.BYTE); // 6 - Yaw
                map(Type.BYTE); // 7 - Pitch
                map(Type.BYTE); // 8 - Head Pitch
                map(Type.SHORT); // 9 - Velocity X
                map(Type.SHORT); // 10 - Velocity Y
                map(Type.SHORT); // 11 - Velocity Z
                map(Types1_14.METADATA_LIST); // 12 - Metadata

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int entityId = wrapper.get(Type.VAR_INT, 0);
                        int type = wrapper.get(Type.VAR_INT, 1);

                        Entity1_14Types.EntityType entType = Entity1_14Types.getTypeFromId(type);

                        // Register Type ID
                        wrapper.user().get(EntityTracker1_14_1.class).addEntity(entityId, entType);

                        metadataRewriter.handleMetadata(entityId, wrapper.get(Types1_14.METADATA_LIST, 0), wrapper.user());
                    }
                });
            }
        });

        // Destroy entities
        protocol.registerOutgoing(State.PLAY, 0x37, 0x37, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT_ARRAY); // 0 - Entity ids
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        for (int entity : wrapper.get(Type.VAR_INT_ARRAY, 0)) {
                            wrapper.user().get(EntityTracker1_14.class).removeEntity(entity);
                        }
                    }
                });
            }
        });

        // Spawn Player
        protocol.registerOutgoing(State.PLAY, 0x05, 0x05, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Type.UUID); // 1 - Player UUID
                map(Type.DOUBLE); // 2 - X
                map(Type.DOUBLE); // 3 - Y
                map(Type.DOUBLE); // 4 - Z
                map(Type.BYTE); // 5 - Yaw
                map(Type.BYTE); // 6 - Pitch
                map(Types1_14.METADATA_LIST); // 7 - Metadata

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int entityId = wrapper.get(Type.VAR_INT, 0);

                        Entity1_14Types.EntityType entType = Entity1_14Types.EntityType.PLAYER;

                        // Register Type ID
                        wrapper.user().get(EntityTracker1_14_1.class).addEntity(entityId, entType);
                        metadataRewriter.handleMetadata(entityId, wrapper.get(Types1_14.METADATA_LIST, 0), wrapper.user());
                    }
                });
            }
        });

        // Entity Metadata
        metadataRewriter.registerMetadataRewriter(0x43, 0x43, Types1_14.METADATA_LIST);
    }
}
