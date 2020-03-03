public interface TextFileSerializable<T> {

    public T deserialize (String text);

    public String serialize (T obj);

}
