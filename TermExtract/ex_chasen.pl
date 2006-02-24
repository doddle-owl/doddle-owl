#!/usr/local/bin/perl -w

#
#  ex_chasen.pl
#
#　ファイルから「茶筅」の形態素解析済みのデータを読み取り
#  標準出力に専門用語とその重要度を返すプログラム
#
#   version 0.30
#
#   maeda@lib.u-tokyo.ac.jp

use TermExtract::Chasen;
#use strict;
my $data = new TermExtract::Chasen;
my $InputFile = $ARGV[0];    # 入力ファイル指定

# プロセスの異常終了時処理
# (ロックディレクトリを使用した場合のみ）
$SIG{INT} = $SIG{QUIT} = $SIG{TERM} = 'sigexit';

# 出力モードを指定
# 1 → 専門用語＋重要度、2 → 専門用語のみ
# 3 → カンマ区切り
my $output_mode = 1;

#
# ドキュメント中の専門用語の頻度を重要度計算に使うかどうか選択
# （デフォルトは使用 $obj->use_frq）
#
#$data->no_frq;  # ドキュメント中の専門用語の頻度を重要度計算に使わない
#$data->use_frq; # ドキュメント中の専門用語の頻度を重要度計算に使う

#
# 重要度計算で、連接語の"延べ数"、"異なり数"、"パープレキシティ"のいず
# れかををとるか選択。パープレキシティは自動で「学習機能なし」になる
# （デフォルトは"延べ数"をとる $obj->use_total)
#
#$data->use_total;      # 延べ数をとる
#$data->use_uniq;       # 異なり数をとる
#$data->use_Perplexity; # パープレキシティをとる(TermExtract 3.04 以上)

#
# 重要度計算で、学習機能を使うかどうか選択
# （デフォルトは、使用する $obj->use_stat)
#
#$data->use_stat; # 学習機能を使う
$data->no_stat;  # 学習機能を使わない

#
# 重要度計算で、「ドキュメント中の用語の頻度」と「連接語の重要度」
# のどちらに比重をおくかを設定する。
# デフォルト値は１
# 値が大きいほど「ドキュメント中の用語の頻度」の比重が高まる
#
#$data->average_rate(0.5);

#
# 学習機能用DBにデータを蓄積するかどうか選択
# 重要度計算で、学習機能を使うときは、セットしておいたほうが
# 無難。処理対象に学習機能用DBに登録されていない語が含まれる
# と正しく動作しない。
# （デフォルトは、蓄積する $obj->use_storage）
#
#$data->use_storage; # 蓄積する
$data->no_storage;  # 蓄積しない

#
# 学習機能用DBに使用するDBMをSDBM_Fileに指定
# （デフォルトは、DB_FileのBTREEモード）
#
#$data->use_SDBM;

# 過去のドキュメントの累積統計を使う場合のデータベースの
# ファイル名をセット
# （デフォルトは "stat.db"と"comb.db"）
#
#$data->stat_db("stat.db");
#$data->comb_db("comb.db");

#
# データベースの排他ロックのための一時ディレクトリを指定
# ディレクトリ名が空文字列（デフォルト）の場合はロックしない
#
#$data->lock_dir("lock_dir");

#
# 形態素解析済みのテキストから、データを読み込み
# 専門用語リストを配列に返す
# （累積統計DB使用、ドキュメント中の頻度使用にセット）
#
#my @noun_list = $data->get_imp_word($str, 'var');     # 入力が変数
my @noun_list = $data->get_imp_word($InputFile); # 入力がファイル

#
# 前回読み込んだ形態素解析済みテキストファイルを元に
# モードを変えて、専門用語リストを配列に返す
#$data->use_stat->no_frq;
#my @noun_list2 = $data->get_imp_word();
# また、その結果を別のモードによる結果と掛け合わせる
#@noun_list = $data->result_filter (\@noun_list, \@noun_list2, 30, 1000);

#print("noun_list: ".@noun_list);

#
#  専門用語リストと計算した重要度を標準出力に出す
#
foreach (@noun_list) {
   # 日付・時刻は表示しない
   next if $_->[0] =~ /^(昭和)*(平成)*(\d+年)*(\d+月)*(\d+日)*(午前)*(午後)*(\d+時)*(\d+分)*(\d+秒)*$/;
   # 数値のみは表示しない
   next if $_->[0] =~ /^\d+$/;

   # 結果表示（$output_modeに応じて、出力様式を変更
   printf "%-60s\t%16.2f\n", $_->[0], $_->[1] if $output_mode == 1;
   printf "%s\n",           $_->[0]          if $output_mode == 2;
   printf "%s,",            $_->[0]          if $output_mode == 3;
}

# プロセスの異常終了時にDBのロックを解除
# (ロックディレクトリを使用した場合のみ）
sub sigexit {
   $data->unlock_db;
}
