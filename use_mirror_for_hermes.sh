#!/bin/zsh

# 定义目标文件路径
TARGET_FILE=$(find node_modules/react-native/sdks/hermes-engine/ -type f -name "hermes-utils.rb" 2>/dev/null)

# 检查目标文件是否存在
if [ -z "$TARGET_FILE" ]; then
  echo "未找到 hermes-utils.rb 文件，请先执行 npm install 安装依赖。" >&2
  exit 1
fi

# 检查并替换指定的 URL
if grep -q 'return "https://repo1.maven.org/maven2/com/facebook/react/react-native-artifacts/' "$TARGET_FILE"; then
  sed -i.bak 's|https://repo1.maven.org/maven2/|http://mirrors.cloud.tencent.com/nexus/repository/maven-public/|' "$TARGET_FILE"
  echo "已成功替换 URL。备份文件保存为 $TARGET_FILE.bak"
  exit 0
fi

# 检查是否已运行过脚本
if grep -q 'def release_tarball_url(version, build_type)' "$TARGET_FILE"; then
  echo "检测到 release_tarball_url 方法，可能已运行过脚本，请自行打开文件查看：$TARGET_FILE" >&2
  exit 1
fi

# 提示源代码架构可能已更新
echo "未找到匹配内容，可能是源代码架构已更新，请更新脚本逻辑。" >&2
exit 1
