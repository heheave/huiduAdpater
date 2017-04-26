package cache.channelrealtimedata;

import cache.DefaultCacheImpl;

public class ChannelRealtimeDataCache extends DefaultCacheImpl<ChannelRealtimeData> {

	private static final ChannelRealtimeDataCache crdc = new ChannelRealtimeDataCache();

	private ChannelRealtimeDataCache() {
	}

	public static ChannelRealtimeDataCache instance() {
		return crdc;
	}

	@Override
	public void load() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}
}
