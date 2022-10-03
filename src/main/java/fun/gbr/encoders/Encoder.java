package fun.gbr.encoders;

public interface Encoder {
	public byte[] convert(byte[] bytes) throws Exception;
	public String getName();
}
