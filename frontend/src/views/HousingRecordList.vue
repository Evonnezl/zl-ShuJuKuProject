<template>
  <div>
    <div class="page-title-row">
      <h2>所有住房记录</h2>
      <span class="text-muted">共 {{ filteredList.length }} 条记录</span>
    </div>

    <div class="glass-card" v-reveal>
      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th style="width:50px;">ID</th>
              <th>户主</th>
              <th>部门</th>
              <th>职称</th>
              <th style="width:70px;">家庭人口</th>
              <th style="width:70px;">分数</th>
              <th>房号</th>
              <th style="width:90px;">面积（㎡）</th>
              <th style="width:100px;">月租金（元）</th>
              <th style="width:110px;">入住日期</th>
              <th style="width:80px;">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in filteredList" :key="item.id">
              <td>{{ item.id }}</td>
              <td><strong>{{ item.userName }}</strong></td>
              <td>{{ item.department }}</td>
              <td>{{ item.titleLevel }}</td>
              <td class="col-center">{{ item.familySize }}人</td>
              <td class="col-center">
                <span class="tag" :class="item.score >= 80 ? 'tag-success' : item.score >= 60 ? 'tag-warning' : 'tag-danger'">
                  {{ item.score }}
                </span>
              </td>
              <td>{{ item.houseTitle }}</td>
              <td class="col-center">{{ item.houseArea }}</td>
              <td class="col-right">{{ item.rentAmount }} 元</td>
              <td>{{ item.moveInDate }}</td>
              <td>
                <button class="btn btn-outline btn-sm" @click="showReceipt(item.id)">分配单</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="filteredList.length === 0" class="empty-state">
        <div class="empty-icon">-</div>
        <p>暂无住房记录</p>
      </div>
    </div>

    <!-- 住房分配单弹窗 -->
    <div v-if="receipt" class="modal-mask" @click.self="receipt=null">
      <div class="receipt-paper" id="receipt-print-area">
        <div class="receipt-header">
          <h2>住房分配单</h2>
          <p class="receipt-sub">房产管理系统 · 正式分配凭证</p>
        </div>

        <div class="receipt-body">
          <table class="receipt-table">
            <tr>
              <td class="label">分配编号</td>
              <td>{{ receipt.recordId }}</td>
              <td class="label">入住日期</td>
              <td>{{ receipt.moveInDate }}</td>
            </tr>
            <tr>
              <td class="label">户主姓名</td>
              <td>{{ receipt.userName }}</td>
              <td class="label">联系电话</td>
              <td>{{ receipt.userPhone }}</td>
            </tr>
            <tr>
              <td class="label">所属部门</td>
              <td>{{ receipt.department }}</td>
              <td class="label">职称</td>
              <td>{{ receipt.titleLevel }}</td>
            </tr>
            <tr>
              <td class="label">家庭人数</td>
              <td>{{ receipt.familySize }} 人</td>
              <td class="label">住房分数</td>
              <td>{{ receipt.score }} 分</td>
            </tr>
            <tr class="divider">
              <td colspan="4"></td>
            </tr>
            <tr>
              <td class="label">分配房号</td>
              <td>{{ receipt.houseTitle }}</td>
              <td class="label">住房面积</td>
              <td>{{ receipt.houseArea }} ㎡</td>
            </tr>
            <tr>
              <td class="label">每平米租金</td>
              <td>{{ receipt.rentPerM2 }} 元</td>
              <td class="label">月租金合计</td>
              <td><strong>{{ receipt.rentAmount }} 元</strong></td>
            </tr>
          </table>
        </div>

        <div class="receipt-footer">
          <p>本分配单一式两份，住户与房管部门各执一份。</p>
          <p>分配日期：{{ receipt.moveInDate }}</p>
        </div>

        <div class="receipt-actions no-print">
          <button class="btn btn-outline" @click="receipt=null">关闭</button>
          <button class="btn btn-primary" @click="printReceipt">打印分配单</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  inject: ['user'],

  data() {
    return {
      list: [],
      receipt: null
    }
  },

  computed: {
    filteredList() {
      if (!this.user || this.user.role === 'admin') return this.list
      return this.list.filter(r => r.userId === this.user.id)
    }
  },

  mounted() {
    this.load()
  },

  methods: {
    load() {
      fetch('http://localhost:8080/records')
        .then(res => res.json())
        .then(data => { this.list = data })
        .catch(err => console.error('加载住房记录失败', err))
    },

    showReceipt(id) {
      fetch(`http://localhost:8080/records/${id}/receipt`)
        .then(res => res.json())
        .then(data => { this.receipt = data })
        .catch(err => console.error('加载分配单失败', err))
    },

    printReceipt() {
      window.print()
    }
  }
}
</script>

<style scoped>
.modal-mask {
  position: fixed; inset: 0;
  background: rgba(0,0,0,.4);
  display: flex; align-items: flex-start; justify-content: center;
  z-index: 1000;
  padding-top: 40px;
  overflow-y: auto;
}

.receipt-paper {
  background: #fff;
  width: 680px;
  max-width: 95vw;
  padding: 40px 48px;
  border-radius: 4px;
  box-shadow: var(--shadow-lg);
  margin-bottom: 40px;
}

.receipt-header {
  text-align: center;
  border-bottom: 2px solid #1e293b;
  padding-bottom: 16px;
  margin-bottom: 24px;
}
.receipt-header h2 {
  font-size: 24px;
  letter-spacing: 4px;
  margin: 0 0 4px;
}
.receipt-sub {
  font-size: 13px;
  color: var(--text-muted);
}

.receipt-table {
  width: 100%;
  border-collapse: collapse;
}
.receipt-table td {
  padding: 10px 12px;
  border: 1px solid #e2e8f0;
  font-size: 14px;
}
.receipt-table .label {
  background: #f8fafc;
  font-weight: 600;
  color: var(--text-secondary);
  width: 120px;
}
.receipt-table .divider td {
  border: none;
  padding: 6px;
}

.receipt-footer {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
  font-size: 13px;
  color: var(--text-muted);
  text-align: center;
}
.receipt-footer p {
  margin: 4px 0;
}

.receipt-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 24px;
}

/* 打印时隐藏侧边栏和按钮，只显示分配单 */
@media print {
  .no-print, .sidebar, .page-header { display: none !important; }
  .main-area { margin-left: 0 !important; }
  .page-body { padding: 0 !important; }
  .modal-mask { position: static; background: none; padding: 0; }
  .receipt-paper { box-shadow: none; width: 100%; max-width: 100%; }
}
</style>
