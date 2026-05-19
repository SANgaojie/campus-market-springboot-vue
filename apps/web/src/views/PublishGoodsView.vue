<!--
  PublishGoodsView 模块

  @author 阿德
  @date 2026/05/14
-->
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { createGoods, fetchCategories } from '@/api/goods'
import { uploadGoodsImage } from '@/api/uploads'
import type { Category } from '@/api/types'

const router = useRouter()
const categories = ref<Category[]>([])
const imageUrls = ref<string[]>([])
const error = ref('')
const uploading = ref(false)
const maxImages = 9
const form = reactive({
  categoryId: 1,
  title: '',
  description: '',
  price: 1,
  conditionLevel: 4,
})

onMounted(async () => {
  categories.value = await fetchCategories()
  if (categories.value[0]) form.categoryId = categories.value[0].id
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
    const goods = await createGoods({
      categoryId: form.categoryId,
      title: form.title,
      description: form.description,
      price: Number(form.price),
      conditionLevel: Number(form.conditionLevel),
      imageUrls: imageUrls.value,
    })
    router.push(`/goods/${goods.id}`)
  } catch (err) {
    error.value = err instanceof Error ? err.message : '发布失败'
  }
}
</script>

<template>
  <section class="card">
    <h1 class="page-title">发布商品</h1>
    <form class="form" @submit.prevent="submit">
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
        商品图片
        <input type="file" accept="image/jpeg,image/png,image/webp,image/gif" multiple @change="handleImageChange" />
      </label>
      <p class="muted">支持 jpg、png、webp、gif，单张不超过 5MB，最多 {{ maxImages }} 张。</p>

      <div v-if="imageUrls.length" class="image-preview-grid">
        <div v-for="(url, index) in imageUrls" :key="url" class="image-preview-card">
          <img :src="url" alt="商品图片预览" />
          <button class="danger" type="button" @click="removeImage(index)">移除</button>
        </div>
      </div>

      <p v-if="uploading" class="muted">图片上传中...</p>
      <p v-if="error" class="error">{{ error }}</p>
      <button class="primary" type="submit" :disabled="uploading">发布</button>
    </form>
  </section>
</template>
