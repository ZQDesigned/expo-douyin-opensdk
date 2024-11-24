import { DouyinOpenSDK } from "expo-douyin-opensdk";
import { Button, SafeAreaView, ScrollView, Text, View } from "react-native";

export default function App() {
  const init = () => {
    DouyinOpenSDK.initSDK("awut6jdxmpif6vzq");
  };

  const isDouyinInstalled = () => {
    return DouyinOpenSDK.isDouyinInstalled();
  };

  const authorize = async () => {
    return await DouyinOpenSDK.authorize(false);
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView style={styles.container}>
        <Text style={styles.header}>Module API</Text>
        <Group name="静态函数">
          <Button title="Init" onPress={init}></Button>
          <Button
            title="是否安装"
            onPress={() => console.log(isDouyinInstalled())}
          ></Button>
        </Group>
        <Group name="主要功能">
          <Button
            title="授权登录"
            onPress={async () => {
              const result = await authorize();
              console.log(result);
            }}
          />
        </Group>
      </ScrollView>
    </SafeAreaView>
  );
}

function Group(props: { name: string; children: React.ReactNode }) {
  return (
    <View style={styles.group}>
      <Text style={styles.groupHeader}>{props.name}</Text>
      {props.children}
    </View>
  );
}

const styles = {
  header: {
    fontSize: 30,
    margin: 20,
  },
  groupHeader: {
    fontSize: 20,
    marginBottom: 20,
  },
  group: {
    margin: 20,
    backgroundColor: '#fff',
    borderRadius: 10,
    padding: 20,
  },
  container: {
    flex: 1,
    backgroundColor: '#eee',
  },
  view: {
    flex: 1,
    height: 200,
  },
};
