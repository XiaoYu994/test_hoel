<template>
    <!-- 用户留言页面容器 -->
    <div class="page-container">
        <!-- 卡片 -->
        <el-card>
            <template #header>
                <!-- 标题 -->
                <span>用户留言（{{ total }}）</span>
                <!-- 添加留言按钮 -->
                <el-button @click="handleAddClick" style="float: right; padding: 3px 0" type="text">吐槽一下</el-button>
            </template>

            <!-- 留言列表 -->
            <div class="message-list" v-loading="listLoading">

                <div class="message-item" v-for="item in list" :key="item.id">
                    <div class="comment-head">
                        <img :src="getAvatarSrc(item.member && item.member.head)" class="head" />
                    </div>
                    <div class="comment-detail p-10">
                        <!-- 评论内容 -->
                        <div class="text-bold">{{ item.member.name }}</div>
                        <div class="comment-content font-14 my-10">{{ item.comment }}</div>
                        <div class="text-gray mb-10 font-14">{{ item.createTime }}</div>
                        <!-- 回复内容 -->
                        <div class="reply p-y" v-show="item?.reply">

                            <div class="reply-detail">
                                <div class="text-bold">管理员回复</div>
                                <div class="reply-content font-14 my-10">{{ item.reply }}</div>
                                <div class="text-gray font-14">{{ item.replyTime }}</div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>

            <!-- 分页器 -->
            <el-pagination class="page-box" background @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :current-page="pageNum" :page-sizes="[10, 20]" :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper" :total="total">
            </el-pagination>
        </el-card>
        <!-- 留言对话框 -->
        <el-dialog title="留言" v-model="showDialog" :before-close="handleDialogClose">
            <!-- 留言输入框 -->
            <el-input type="textarea" :rows="4" placeholder="请输入你的留言" v-model="dialogForm.comment">
            </el-input>
            <!-- 留言对话框底部 -->
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showDialog = false">取 消</el-button>
                    <el-button type="primary" @click="dialogConfirm">确 定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { findMessagePageAPI, addMessageAPI } from '@/api/message'

defineOptions({ name: 'FrontMessage' })

const list = ref([])
const listLoading = ref(false)
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)
const showDialog = ref(false)
const dialogForm = ref({})

const authStore = useAuthStore()
const { token } = storeToRefs(authStore)
const router = useRouter()
const { appContext } = getCurrentInstance()
const baseFileUrl = appContext.config.globalProperties.$baseFileUrl

function getAvatarSrc(head) {
  if (!head) return require('@/assets/head.jpg')
  if (/^(https?:)?\/\//.test(head) || head.startsWith('data:')) return head
  const base = typeof baseFileUrl === 'string' ? baseFileUrl : ''
  if (!base) return head
  const normalizedBase = base.endsWith('/') ? base.slice(0, -1) : base
  const normalizedHead = head.startsWith('/') ? head.slice(1) : head
  return `${normalizedBase}/${normalizedHead}`
}

async function fetchData() {
  listLoading.value = true
  try {
    const response = await findMessagePageAPI(
      pageNum.value,
      pageSize.value,
      undefined
    )
    list.value = response.data.records
    total.value = response.data.total
  } finally {
    listLoading.value = false
  }
}

function handleDialogClose() {
  showDialog.value = false
}

function handleSizeChange(val) {
  pageSize.value = val
  fetchData()
}

function handleCurrentChange(val) {
  pageNum.value = val
  fetchData()
}

function handleSearch() {
  fetchData()
}

function handleAddClick() {
  if (!token.value) {
    ElMessage.error('请先登录！')
    setTimeout(() => router.push('/login'), 1000)
    return
  }
  showDialog.value = true
}

async function dialogConfirm() {
  const comment = dialogForm.value.comment
  if (!comment || comment === '') {
    ElMessage.error('内容不能为空！')
    return
  }
  const res = await addMessageAPI(dialogForm.value)
  ElMessage[res.flag ? 'success' : 'error'](res.message)
  if (res.flag) {
    showDialog.value = false
    fetchData()
  }
}

onMounted(fetchData)
</script>

<style lang="less" scoped>
.page-container {
    width: 1200px;
    margin: 20px auto;

    .message-list {
        .message-item {
            display: flex;
            line-height: 1.5;
            margin-bottom: 10px;
            border-bottom: 1px solid #ccc;
            font-size: 14px;

            &:nth-last-child(1) {
                border-bottom: none;
            }

            .head {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                object-fit: cover;
            }

            .reply {
                display: flex;
            }
        }
    }
}
</style>