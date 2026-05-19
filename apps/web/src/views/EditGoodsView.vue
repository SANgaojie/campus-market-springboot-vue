<!--
  EditGoodsView 模块

  @author 阿德
  @date 2026/05/06
-->
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchCategories, fetchMyGoodsDetail, updateGoods } from '@/api/goods'
import { uploadGoodsImage } from '@/api/uploads'
import type { Category } from '@/api/types'

const props = defineProps<{ id: string }>()
const router = useRouter()
const categories = ref<Category[]>([])
const imageUrls = ref<string[]>([])
const loading = ref(true)
const uploading = ref(false)
const error = ref('')
const maxImages = 9
const form = reactive({
  categoryId: 1,
  title: '',
  description: '',
  price: 1,
  conditionLevel: 4,
})

onMounted(async () => {
  error.value = ''
  loading.value = true
  try {
    const [categoryList, goods] = await Promise.all([
      fetchCategories(),
      fetchMyGoodsDetail(Number(props.id)),
    ])
    categories.value = categoryList
    form.categoryId = goods.categoryId
    form.title = goods.title
    form.description = goods.description ?? ''
    form.price = Number(goods.price)
    form.conditionLevel = goods.conditionLevel
    imageUrls.value = [...goods.imageUrls]
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载商品失败'
  } finally {
    loading.value = false
  }
})

async function handleImageChange(event: Event) {
  const input = event.target as HTMLInputElement
  const files = Array.from(input.files ?? [])
  if (files.length === 0) return

  error.value = ''
  const availableSlots = maxImages - imageUrls.value.length
  if (availableSlots <= 0) {
    error.value = `最多上传 ${maxImages} 张图片`
    input.value = ''
    return
  }

  uploading.value = true
  try {
    for (const file of files.slice(0, availableSlots)) {
      const result = await uploadGoodsImage(file)
      imageUrls.value.push(result.url)
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : '图片上传失败'
  } finally {
    uploading.value = false
    input.value = ''
  }
}

function removeImage(index: number) {
  imageUrls.value.splice(index, 1)
}

async function submit() {
  error.value = ''
  try {
    const goods = await updateGoods(Number(props.id), {
      categoryId: form.categoryId,
      title: form.title,
      description: form.description,
      price: Number(form.price),
      conditionLevel: Number(form.conditionLevel),
      imageUrls: imageUrls.value,
    })
    router.push(`/goods/${goods.id}`)
  } catch (err) {
    error.value = err instanceof Error ? err.message : '保存失败'
  }
}
</script>

<template>
  <section class="card">
    <div class="page-header-row">
      <div>
        <h1 class="page-title">编辑商品</h1>
        <p class="muted">交易中或已售出的商品不可编辑。</p>
      </div>
      <RouterLink class="secondary" to="/my-goods">返回我的商品</RouterLink>
    </div>

    <p v-if="loading" class="muted">加载中...</p>
    <form v-else class="form" @submit.prevent="submit">
      <label>
        分类
        <select v-model.number="form.categoryId">
          <option v-for="category in categories" :key="category.id" :value="category.id">
            {{ category.name }}
          </option>
        </select>
      </label>
      <label>
        标题
        <input v-model="form.title" required maxlength="100" />
      </label>
      <label>
        描述
        <textarea v-model="form.description" maxlength="2000" />
      </label>
      <label>
        价格
        <input v-model.number="form.price" type="number" min="0.01" step="0.01" required />
      </label>
      <label>
        成色
        <select v-model.number="form.conditionLevel">
          <option :value="5">5 - 几乎全新</option>
          <option :value="4">4 - 很新</option>
          <option :value="3">3 - 正常使用</option>
          <option :value="2">2 - 明显使用痕迹</option>
          <option :value="1">1 - 功能可用</option>
        </select>
      </label>
      <label>
        追加商品图片
        <input type="file" accept="image/jpeg,image/png,image/webp,image/gif" multiple @change="handleImageChange" />
      </label>
      <p class="muted">支持 jpg、png、webp、gif，单张不超过 5MB，最多 {{ maxImages }} 张。</p>

      <div v-if="imageUrls.length" class="image-preview-grid">
        <div v-for="(url, index) in imageUrls" :key="`${url}-${index}`" class="image-preview-card">
          <img :src="url" alt="商品图片预览" />
          <button class="danger" type="button" @click="removeImage(index)">移除</button>
        </div>
      </div>

      <p v-if="uploading" class="muted">图片上传中...</p>
      <p v-if="error" class="error">{{ error }}</p>
      <button class="primary" type="submit" :disabled="uploading">保存修改</button>
    </form>
  </section>
</template>
